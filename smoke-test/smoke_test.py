"""
Smoke de integracion (cableado, no logica de negocio) entre mk-tournament-service
y am-matches-service. Corre contra el docker-compose.yml de esta misma carpeta.
Ver README.md para como levantar el entorno. Requiere: python3, la libreria
`requests`, y el CLI de docker (para leer logs y sembrar Mongo via mongosh).
"""

import base64
import json
import subprocess
import sys
import time
import uuid
from datetime import date, datetime, timedelta, timezone

import requests

TOURNAMENT_BASE = "http://localhost:5623"
MATCHES_BASE = "http://localhost:8090"
RABBIT_MGMT_BASE = "http://localhost:15672"
RABBIT_AUTH = ("guest", "guest")

INTERNAL_API_KEY = "local-smoke-test-key"
WRONG_API_KEY = "esta-llave-esta-mal"

MONGO_CONTAINER = "smoke-mongo-tournament"
MONGO_DB = "techcup_tournaments"
TOURNAMENT_CONTAINER = "smoke-tournament"

RESULTS = []


def report(name, ok, detail=""):
    RESULTS.append((name, ok))
    status = "PASS" if ok else "FAIL"
    line = f"[{status}] {name}"
    if detail:
        line += f"\n       {detail}"
    print(line)
    return ok


def b64url(obj):
    raw = json.dumps(obj).encode("utf-8")
    return base64.urlsafe_b64encode(raw).rstrip(b"=").decode("ascii")


def unsigned_referee_jwt(referee_id):
    # JwtClaimsFilter (am-matches-service) decodifica los claims sin verificar
    # firma: alcanza con header.payload (sin tercer segmento) para autenticar
    # como arbitro en este harness. Ver hallazgo documentado en el analisis previo.
    header = {"alg": "none", "typ": "JWT"}
    payload = {"sub": str(referee_id), "roles": ["ARBITRO"]}
    return f"{b64url(header)}.{b64url(payload)}"


def referee_headers(referee_id):
    return {"Authorization": f"Bearer {unsigned_referee_jwt(referee_id)}"}


def wait_http_ok(url, timeout=90, interval=2):
    deadline = time.time() + timeout
    last_err = None
    while time.time() < deadline:
        try:
            r = requests.get(url, timeout=5)
            if r.status_code < 500:
                return True
        except requests.RequestException as e:
            last_err = e
        time.sleep(interval)
    print(f"Timeout esperando {url} ({last_err})")
    return False


def wait_for_log_containing(needle, container=TOURNAMENT_CONTAINER, timeout=20, interval=2):
    deadline = time.time() + timeout
    while time.time() < deadline:
        result = subprocess.run(
            ["docker", "logs", "--tail", "500", container],
            capture_output=True, text=True,
        )
        if needle in (result.stdout + result.stderr):
            return True
        time.sleep(interval)
    return False


def sample_match_definition():
    return {
        "matchId": str(uuid.uuid4()),
        "tournamentId": str(uuid.uuid4()),
        "fase": "GRUPOS",
        "equipoAId": str(uuid.uuid4()),
        "equipoBId": str(uuid.uuid4()),
        "equipoANombre": "Equipo Local Smoke",
        "equipoBNombre": "Equipo Visitante Smoke",
        "fecha": (date.today() + timedelta(days=1)).isoformat(),
        "hora": "10:00:00",
        "arbitroId": str(uuid.uuid4()),
        "canchaId": str(uuid.uuid4()),
    }


# --------------------------------------------------------------------------
# CHEQUEO 1: recepcion + autenticacion servicio-a-servicio en matches
# --------------------------------------------------------------------------

def cheque_1_recepcion_y_auth():
    print("\n=== CHEQUEO 1: recepcion + auth en matches (POST /api/partidos) ===")

    r_ok = requests.post(
        f"{MATCHES_BASE}/api/partidos", json=sample_match_definition(),
        headers={"X-Internal-Api-Key": INTERNAL_API_KEY}, timeout=10)
    ok1 = report(
        "1a) POST /api/partidos con X-Internal-Api-Key correcta -> 2xx",
        r_ok.status_code in (200, 201),
        f"status={r_ok.status_code} body={r_ok.text[:200]}")

    r_missing = requests.post(
        f"{MATCHES_BASE}/api/partidos", json=sample_match_definition(), timeout=10)
    ok2 = report(
        "1b) POST /api/partidos sin el header -> 401/403",
        r_missing.status_code in (401, 403),
        f"status={r_missing.status_code}")

    r_wrong = requests.post(
        f"{MATCHES_BASE}/api/partidos", json=sample_match_definition(),
        headers={"X-Internal-Api-Key": WRONG_API_KEY}, timeout=10)
    ok3 = report(
        "1c) POST /api/partidos con llave incorrecta -> 401/403",
        r_wrong.status_code in (401, 403),
        f"status={r_wrong.status_code}")

    return ok1 and ok2 and ok3


# --------------------------------------------------------------------------
# CHEQUEO 2: binding del exchange + forma del DTO en tournament
# --------------------------------------------------------------------------

def cheque_2_broker_y_dto():
    print("\n=== CHEQUEO 2: broker + DTO en tournament (techcup.exchange) ===")

    marker_tournament_id = str(uuid.uuid4())
    event = {
        "matchId": str(uuid.uuid4()),
        "tournamentId": marker_tournament_id,
        "fase": "GRUPOS",
        "golesA": 2,
        "golesB": 0,
        "ganadorId": str(uuid.uuid4()),
        "eliminadoId": None,
        "ausenteId": None,
        "finishedAt": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
    }
    publish_body = {
        "properties": {"content_type": "application/json"},
        "routing_key": "techcup.match.finished",
        "payload": json.dumps(event),
        "payload_encoding": "string",
    }
    r = requests.post(
        f"{RABBIT_MGMT_BASE}/api/exchanges/%2f/techcup.exchange/publish",
        auth=RABBIT_AUTH, json=publish_body, timeout=10)
    published = report(
        "2a) publicar en techcup.exchange (routing_key=techcup.match.finished) -> routed=true",
        r.status_code == 200 and r.json().get("routed") is True,
        f"status={r.status_code} body={r.text[:200]}")
    if not published:
        return False

    # No hace falta que el torneo/matchId existan: lo que valida este chequeo es
    # que el listener reciba y deserialice el evento (no perdida silenciosa). Al
    # no existir el torneo, ProcessMatchResultService lanza TournamentNotFoundException
    # con el id que mandamos, y eso queda en el log -- prueba de recepcion + DTO ok.
    received = report(
        "2b) tournament recibio y deserializo el evento (aparece en logs)",
        wait_for_log_containing(marker_tournament_id, timeout=20),
        f"buscado en logs de '{TOURNAMENT_CONTAINER}': {marker_tournament_id}")

    return published and received


# --------------------------------------------------------------------------
# CHEQUEO 3: un partido real, de punta a punta
# --------------------------------------------------------------------------

def mongo_seed_enrollments(tournament_id, teams):
    """Siembra directo en Mongo 8 equipos APPROVED + inscripciones ENROLLED,
    sin pasar por team-service/payment-service (que este harness no levanta).
    Ver README: decision documentada de usar seed directo en vez de mockear
    el Feign client de team-service."""
    teams_docs = [
        {"teamId": tid, "teamName": name, "registrationStatus": "APPROVED", "points": 0}
        for tid, name in teams
    ]
    enrollments_docs = [
        {"enrollmentId": str(uuid.uuid4()), "teamId": tid, "teamName": name,
         "status": "ENROLLED", "points": 0}
        for tid, name in teams
    ]
    script = (
        f'db.tournaments.updateOne('
        f'{{_id: "{tournament_id}"}}, '
        f'{{$set: {{teams: {json.dumps(teams_docs)}, enrollments: {json.dumps(enrollments_docs)}}}}}'
        f');'
    )
    result = subprocess.run(
        ["docker", "exec", "-i", MONGO_CONTAINER, "mongosh", MONGO_DB, "--quiet", "--eval", script],
        capture_output=True, text=True,
    )
    return result.returncode == 0, (result.stdout + result.stderr)


def cheque_3_partido_real():
    print("\n=== CHEQUEO 3: partido real de punta a punta ===")
    referee_id = str(uuid.uuid4())
    ref_headers = referee_headers(referee_id)
    today = date.today()

    create_body = {
        "name": "Smoke Test Cup",
        "type": "NORMAL",
        "format": "GROUPS",
        "numberOfTeams": 8,
        "cost": 0,
        "startDate": (today + timedelta(days=10)).isoformat(),
        "endDate": (today + timedelta(days=30)).isoformat(),
        "registrationDeadline": (today + timedelta(days=5)).isoformat(),
    }
    r = requests.post(f"{TOURNAMENT_BASE}/tournaments", json=create_body, timeout=10)
    if not report("3.1) crear torneo (DRAFT)", r.status_code == 201,
                   f"status={r.status_code} body={r.text[:300]}"):
        return False
    tournament_id = r.json()["id"]

    r = requests.patch(f"{TOURNAMENT_BASE}/tournaments/{tournament_id}/activate", timeout=10)
    if not report("3.2) activar torneo -> ACTIVE", r.status_code == 200,
                   f"status={r.status_code} body={r.text[:300]}"):
        return False

    r = requests.post(f"{TOURNAMENT_BASE}/tournaments/{tournament_id}/courts",
                       files={"section": (None, "CANCHA_1")}, timeout=10)
    if not report("3.3) registrar cancha", r.status_code == 201,
                   f"status={r.status_code} body={r.text[:300]}"):
        return False
    court_id = r.json()["courtId"]

    teams = [(str(uuid.uuid4()), f"Equipo Smoke {i}") for i in range(8)]
    seed_ok, seed_log = mongo_seed_enrollments(tournament_id, teams)
    if not report("3.4) seed directo en Mongo: 8 equipos APPROVED + ENROLLED", seed_ok, seed_log[:300]):
        return False

    r = requests.patch(f"{TOURNAMENT_BASE}/tournaments/{tournament_id}/prepare", timeout=10)
    if not report("3.5) preparar torneo -> genera fixture de grupos", r.status_code == 200,
                   f"status={r.status_code} body={r.text[:300]}"):
        return False

    r = requests.patch(f"{TOURNAMENT_BASE}/tournaments/{tournament_id}/begin", timeout=10)
    if not report("3.6) iniciar torneo -> IN_PROGRESS", r.status_code == 200,
                   f"status={r.status_code} body={r.text[:300]}"):
        return False

    r = requests.get(f"{TOURNAMENT_BASE}/tournaments/{tournament_id}/matchups", timeout=10)
    matchups = r.json() if r.status_code == 200 else []
    candidate = next((m for m in matchups if m.get("homeTeamId") and m.get("awayTeamId")), None)
    if not report("3.7) obtener un partido de grupos del fixture", candidate is not None,
                   f"status={r.status_code} count={len(matchups)}"):
        return False
    match_id = candidate["matchId"]
    home_team_id = candidate["homeTeamId"]
    away_team_id = candidate["awayTeamId"]
    team_names = dict(teams)

    # matches exige que la fecha/hora programada ya haya pasado (en UTC) para
    # poder "iniciar" -- ver MatchServiceImpl.startMatch (LocalDateTime.now(UTC)
    # .isBefore(kickoff) -> 409). Se programa "ahora mismo" en vez de a futuro
    # para poder arbitrar el partido en el mismo run.
    kickoff = datetime.now(timezone.utc) - timedelta(minutes=1)
    schedule_body = {
        "matchupId": match_id,
        "matchDate": kickoff.date().isoformat(),
        "matchTime": kickoff.time().replace(microsecond=0).isoformat(),
        "courtId": court_id,
        "refereeId": referee_id,
    }
    r = requests.post(f"{TOURNAMENT_BASE}/matches", json=schedule_body, timeout=10)
    if not report("3.8) programar partido en tournament -> dispara push hacia matches",
                   r.status_code == 201, f"status={r.status_code} body={r.text[:300]}"):
        return False

    # matches solo expone GET /api/partidos/{id} con SU id interno (`id`), no con
    # `competenciaMatchId` (el matchId que conoce tournament) -- ver MatchAccessService
    # .requireOwnedMatch, que busca por matchRepository.findById(...). Para confirmar
    # la llegada sin inventarnos un endpoint que no existe, usamos el listado "mis
    # partidos asignados" (GET /api/partidos, filtrado por el arbitro autenticado) y
    # correlacionamos por competenciaMatchId, que si viaja en ese listado.
    arrived = None
    deadline = time.time() + 20
    while time.time() < deadline:
        resp = requests.get(f"{MATCHES_BASE}/api/partidos", headers=ref_headers, timeout=5)
        if resp.status_code == 200:
            arrived = next((m for m in resp.json() if m.get("competenciaMatchId") == match_id), None)
            if arrived:
                break
        time.sleep(2)
    arrived_ok = (
        arrived is not None
        and arrived.get("status") == "SCHEDULED"
        and arrived.get("homeTeamName") == team_names.get(home_team_id)
        and arrived.get("awayTeamName") == team_names.get(away_team_id)
    )
    if not report("3.9) confirmar en matches (GET /api/partidos, listado del arbitro): llego con los equipos correctos",
                   arrived_ok, f"arrived={arrived}"):
        return False
    internal_id = arrived["id"]

    # Unico endpoint de matches que sigue usando competenciaMatchId (el matchId de
    # tournament) en vez del id interno -- ver MatchController: @PostMapping
    # ("/{competenciaMatchId}/iniciar"). Todos los demas (goles, finalizar) usan
    # el id interno, capturado arriba en 3.9.
    r = requests.post(f"{MATCHES_BASE}/api/partidos/{match_id}/iniciar", headers=ref_headers, timeout=10)
    if not report("3.10) iniciar partido en matches (via competenciaMatchId)", r.status_code == 201,
                   f"status={r.status_code} body={r.text[:200]}"):
        return False

    for i in range(2):
        r = requests.post(
            f"{MATCHES_BASE}/api/partidos/{internal_id}/goles",
            json={"teamId": home_team_id, "playerId": str(uuid.uuid4()), "minute": 10 + i},
            headers=ref_headers, timeout=10)
        if r.status_code != 201:
            report("3.11) registrar goles (2-0 para el local)", False,
                   f"status={r.status_code} body={r.text[:200]}")
            return False
    report("3.11) registrar goles (2-0 para el local)", True)

    r = requests.post(f"{MATCHES_BASE}/api/partidos/{internal_id}/finalizar", headers=ref_headers, timeout=10)
    if not report("3.12) finalizar partido en matches -> publica techcup.match.finished",
                   r.status_code == 200, f"status={r.status_code} body={r.text[:200]}"):
        return False

    def home_row(standings):
        for group in standings:
            for row in group.get("standings", []):
                if row.get("teamId") == home_team_id and row.get("played", 0) >= 1:
                    return row
        return None

    updated_row = None
    deadline = time.time() + 20
    while time.time() < deadline:
        r = requests.get(f"{TOURNAMENT_BASE}/tournaments/{tournament_id}/standings", timeout=10)
        if r.status_code == 200:
            updated_row = home_row(r.json())
            if updated_row:
                break
        time.sleep(2)
    table_ok = updated_row is not None and updated_row.get("points") == 3
    report(
        "3.13) tabla de grupo se actualiza sola (push -> arbitraje -> evento -> tabla)",
        table_ok, f"fila={updated_row}")

    return table_ok


def main():
    print("Esperando a que tournament y matches respondan...")
    tournament_up = wait_http_ok(f"{TOURNAMENT_BASE}/tournaments/history")
    matches_up = wait_http_ok(f"{MATCHES_BASE}/actuator/health")
    if not (tournament_up and matches_up):
        print("Los servicios no respondieron a tiempo. Revisa 'docker compose logs'.")
        sys.exit(2)

    ok1 = cheque_1_recepcion_y_auth()
    ok2 = cheque_2_broker_y_dto()
    ok3 = cheque_3_partido_real()

    print("\n" + "=" * 70)
    print("RESUMEN")
    print("=" * 70)
    for name, ok in RESULTS:
        print(f"  [{'PASS' if ok else 'FAIL'}] {name}")
    print("-" * 70)
    print(f"CHEQUEO 1 (recepcion + auth):        {'PASS' if ok1 else 'FAIL'}")
    print(f"CHEQUEO 2 (broker + DTO):             {'PASS' if ok2 else 'FAIL'}")
    print(f"CHEQUEO 3 (partido real end-to-end):  {'PASS' if ok3 else 'FAIL'}")
    print("=" * 70)

    if ok1 and ok2 and ok3:
        print("RESULTADO FINAL: PASS")
        sys.exit(0)
    else:
        print("RESULTADO FINAL: FAIL")
        sys.exit(1)


if __name__ == "__main__":
    main()
