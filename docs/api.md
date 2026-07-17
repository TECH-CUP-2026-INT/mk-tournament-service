# REST API

Base URL: `http://localhost:5623`

!!! warning "Access control"
    All endpoints are currently **open** (`permitAll()` in `SecurityConfig`). There is no Identity Service with JWT/roles in the platform yet. The **Intended role** column describes the business intent, not an actual restriction. No endpoint should be left public like this in production.

---

## Tournament management — `/tournaments`

Controller: `TournamentController`.

| Method | Endpoint | Description | Intended role |
|---|---|---|---|
| `POST` | `/tournaments` | Create tournament (starts in `DRAFT`) | Organizer |
| `PATCH` | `/tournaments/{id}` | Edit any tournament field | Organizer |
| `PATCH` | `/tournaments/{id}/activate` | Activate tournament: `DRAFT` → `ACTIVE`, opens enrollment | Organizer |
| `PATCH` | `/tournaments/{id}/prepare` | Start tournament preparation: `ACTIVE` → `IN_PREPARATION`, generates the fixture (group matchups, or the bracket for a `BRACKETS` tournament) | Organizer |
| `PATCH` | `/tournaments/{id}/begin` | Begin the tournament: `IN_PREPARATION` → `IN_PROGRESS`, matches can now be scheduled and played | Organizer |
| `PATCH` | `/tournaments/{id}/finalize` | Finalize tournament (moves to history) | Organizer |
| `PATCH` | `/tournaments/{id}/pause` | Pause / resume tournament (`action: PAUSE \| RESUME`) | Organizer |
| `PATCH` | `/tournaments/{id}/inactivate` | Inactivate / reactivate tournament (`action: INACTIVATE \| REACTIVATE`) | Organizer |
| `DELETE` | `/tournaments/{id}` | Delete tournament (only if Finished) | Organizer |
| `GET` | `/tournaments/{tournamentId}/preparation` | Check preparation readiness | Authenticated |
| `GET` | `/tournaments/active` | Resolve the tournament currently `IN_PROGRESS` (service-to-service) | Service |

### Get active tournament

```http
GET /tournaments/active
```

Integration point consumed by **Estadísticas** (`TournamentClientImpl`) to resolve which tournament is currently live. "Active" here means `IN_PROGRESS` — if more than one tournament is `IN_PROGRESS`, the one with the most recent `startDate` wins (tie-broken deterministically by ID).

**200 response** — contract is exact, do not change the field name or type:

```json
{ "id": "550e8400-e29b-41d4-a716-446655440000" }
```

**404** if no tournament is currently `IN_PROGRESS`.

### Create tournament

```http
POST /tournaments
Content-Type: application/json
```

```json
{
  "name": "TechCup Fútbol 2026-2",
  "type": "NORMAL",
  "format": "BRACKETS",
  "numberOfTeams": 16,
  "cost": 50000,
  "startDate": "2026-08-01",
  "endDate": "2026-09-30",
  "registrationDeadline": "2026-07-25",
  "matchStartTime": null,
  "matchEndTime": null
}
```

**201 response:** `TournamentResponse` with the tournament created in `DRAFT` status.

Lifecycle: `DRAFT` → (`activate`) → `ACTIVE` → (`prepare`, once enough teams are enrolled) → `IN_PREPARATION` → (`begin`) → `IN_PROGRESS` → `FINISHED`. For a `GROUPS` tournament, `prepare` requires exactly 8, 16 or 32 enrolled teams and generates the group-stage matchups; the elimination bracket is generated automatically once every group match is finished (or via `POST /tournaments/{tournamentId}/bracket`, see below).

### Pause / resume tournament

```http
PATCH /tournaments/{id}/pause
Content-Type: application/json
```

```json
{ "action": "PAUSE" }
```

### Inactivate / reactivate tournament

```http
PATCH /tournaments/{id}/inactivate
Content-Type: application/json
```

```json
{ "action": "INACTIVATE" }
```

---

## Enrollments — `/tournaments/{tournamentId}/enrollments`

| Method | Endpoint | Description | Intended role |
|---|---|---|---|
| `POST` | `/tournaments/{tournamentId}/enrollments` | Enroll team | Captain |
| `GET` | `/tournaments/{tournamentId}/enrollments` | View enrolled and reserved teams | Authenticated |

### Enroll team

```http
POST /tournaments/{tournamentId}/enrollments
Content-Type: application/json
```

```json
{ "teamId": "team_xyz789" }
```

**201 response:** `EnrollmentResponse` — `enrollmentId`, `status` (`PENDING_PAYMENT`, …), `reservationExpiresAt`.

---

## Teams and users in a tournament

| Method | Endpoint | Description | Intended role |
|---|---|---|---|
| `GET` | `/tournaments/{tournamentId}/teams` | List registered teams | Authenticated |
| `PATCH` | `/tournaments/{tournamentId}/teams/{teamId}/disqualify` | Disqualify team | Organizer |
| `PATCH` | `/tournaments/{tournamentId}/teams/{teamId}/inactivate` | Inactivate team in the tournament | Organizer |
| `PATCH` | `/tournaments/{tournamentId}/users/{userId}/inactivate` | Inactivate user in the tournament | Organizer |

---

## Brackets and matches

| Method | Endpoint | Description | Intended role |
|---|---|---|---|
| `GET` | `/tournaments/{tournamentId}/matchups` | View bracket / matchups with results | Public |
| `GET` | `/tournaments/{tournamentId}/standings` | Group-stage standings (one table per group, computed on the fly from finished matches) | Public |
| `POST` | `/tournaments/{tournamentId}/bracket` | Manually generate the elimination bracket (normally auto-triggered once every group match is finished) | Organizer |
| `GET` | `/tournaments/{tournamentId}/bracket` | View the elimination bracket nodes (round, slots, winner/loser, next node) | Public |
| `POST` | `/tournaments/{tournamentId}/matches/{matchId}/penalty-shootout` | Record the penalty-shootout winner for a node stuck `PENDING_PENALTIES` (regulation-time tie in an elimination match) | Organizer |
| `GET` | `/tournaments/matches/{matchId}/court` | View the court assigned to a match | Authenticated |
| `POST` | `/tournaments/{tournamentId}/matches/{matchId}/champion` | Assign champion (final match finished) | Organizer |
| `GET` | `/tournaments/{tournamentId}/champion` | Get the tournament champion | Public |

---

## Matches — `/matches`

Controller: `MatchController`.

| Method | Endpoint | Description | Intended role |
|---|---|---|---|
| `POST` | `/matches` | Schedule match (matchup + court + referee + date/time) | Organizer |
| `PATCH` | `/matches/{matchId}/activation` | Inactivate / reactivate a match (`action: INACTIVATE \| REACTIVATE`) | Organizer |
| `POST` | `/matches/{matchId}/resend-definition` | Manually retry pushing the match definition to Matches, for a match whose original push failed | Organizer |

### Schedule match

```http
POST /matches
Content-Type: application/json
```

```json
{
  "matchupId": "m01",
  "matchDate": "2026-08-05",
  "matchTime": "09:00:00",
  "courtId": "court-1",
  "refereeId": "ref-1"
}
```

Scheduling a match also pushes its definition to the **Matches** service (`POST {matches-service.base-url}/api/partidos`, authenticated with the `X-Internal-Api-Key` header — see `matches-service.base-url` / `matches-service.internal-api-key` in `application.yml`). This push is synchronous but **not** allowed to fail the scheduling call itself: if Matches is unreachable or rejects the push, the match is marked `definitionSyncPending` (logged as an error) instead of throwing, and can be retried later with `POST /matches/{matchId}/resend-definition` — which, unlike the automatic push, does propagate the failure to the caller.

### Resend match definition

```http
POST /matches/{matchId}/resend-definition
```

**200 response:** empty body. Re-sends the same `MatchDefinition` payload described above; throws if Matches still rejects it.

### Inactivate / reactivate match

```http
PATCH /matches/{matchId}/activation
Content-Type: application/json
```

```json
{ "action": "INACTIVATE" }
```

**200 response:**

```json
{
  "matchId": "m01",
  "active": false,
  "message": "The match was successfully inactivated"
}
```

An inactive match **keeps the data already recorded** (score, status) but blocks `finish()`, recording the penalty shootout winner, and marking a no-show. Cards, substitutions and clock management are not implemented in this service — they are the responsibility of the future **Match Service**, which must check this status before accepting those events.

---

## Asynchronous integration — RabbitMQ

This service both publishes to and consumes from a shared topic exchange, **`techcup.exchange`** (durable, declared in `RabbitMQConfig`). Configuration (host/credentials/exchange name) is env-driven — see `spring.rabbitmq.*` and `techcup.rabbitmq.exchange` in `application.yml`.

**Consumes** — `techcup.match.finished` (routing key), queue `techcup.tournament.match-finished`, handled by `MatchFinishedListener`. Published by Matches when a match is finished. Payload (`MatchFinishedEvent`):

```json
{
  "matchId": "m01",
  "tournamentId": "t01",
  "fase": "GRUPOS",
  "golesA": 2,
  "golesB": 1,
  "ganadorId": "team-A",
  "eliminadoId": null,
  "ausenteId": null,
  "finishedAt": "2026-08-05T12:34:56Z"
}
```

The listener delegates entirely to `ProcessMatchResultUseCase`, the same use case `POST /sim/partidos/{matchId}/resultado` calls directly (see below). `ausenteId` non-null means the match was a walkover: the present team wins administratively (3 points, 0-0 in the group table), the absent team is marked `FINISHED_NO_SHOW`. Processing this event is what automatically closes the group stage (generating the elimination bracket once every group match is resolved), advances the elimination bracket, and serves one match of suspension for every active sanction via `RecordMatchFinishedForSanctionsUseCase` (see the note on `POST /sanctions/match-finished` below).

**Publishes** — the outbound push of a match definition to Matches (`POST /api/partidos`, see "Schedule match" above) is a plain synchronous REST call, not a RabbitMQ message.

---

## Simulation (dev only) — `/sim`

Controller: `SimulationController`, only registered under the `dev` Spring profile. Lets you trigger `ProcessMatchResultUseCase` directly — same code path as the `techcup.match.finished` listener — without needing a RabbitMQ broker or the Matches service running.

| Method | Endpoint | Description | Intended role |
|---|---|---|---|
| `POST` | `/sim/partidos/{matchId}/resultado` | Simulate a match result for the given tournament matchId | Dev only |

```http
POST /sim/partidos/{matchId}/resultado
Content-Type: application/json
```

```json
{
  "golesA": 2,
  "golesB": 1,
  "ganadorId": "team-A",
  "eliminadoId": null,
  "ausenteId": null,
  "fase": "GRUPOS"
}
```

Same field shape as `MatchFinishedEvent` (see above), minus `matchId`/`tournamentId` (resolved from the URL) and `finishedAt` (unused).

---

## Courts

| Method | Endpoint | Description | Intended role |
|---|---|---|---|
| `POST` | `/tournaments/{tournamentId}/courts` | Register court (multipart: `section`, `description`, `image`) | Organizer |

---

## Rulebook

| Method | Endpoint | Description | Intended role |
|---|---|---|---|
| `POST` | `/tournaments/{tournamentId}/rulebook` | Attach rulebook (PDF, multipart `file`) | Organizer |
| `GET` | `/tournaments/{tournamentId}/rulebook` | View / download rulebook | Public |

---

## Tournament history

| Method | Endpoint | Description | Intended role |
|---|---|---|---|
| `GET` | `/tournaments/history` | List all finished tournaments | Public |
| `GET` | `/tournaments/history/{tournamentId}` | Detail of a historical tournament | Public |

---

## Sanctions — `/sanctions`

Controller: `SanctionController`.

| Method | Endpoint | Description | Intended role |
|---|---|---|---|
| `POST` | `/sanctions` | Apply sanction to a player | Referee / Organizer |
| `GET` | `/sanctions/{playerId}` | View a player's active sanctions | Authenticated |
| `POST` | `/sanctions/match-finished` | Manually serve one match of suspension for every active sanction (same use case, `RecordMatchFinishedForSanctionsUseCase`) | *(redundant for normal operation — `ProcessMatchResultService` already calls this use case automatically on every match result, whether it arrived via the RabbitMQ listener or `/sim`; this endpoint remains available as a manual trigger)* |

---

## Audit — `/audit-events`

Controller: `AuditEventController`. Every successful action in the application services is captured automatically via an AOP aspect (`AuditEventAspect`).

| Method | Endpoint | Description | Intended role |
|---|---|---|---|
| `GET` | `/audit-events` | Query events, with optional filters combined with AND: `from`, `to`, `eventType`, `tournamentId` | Admin / Organizer |

```http
GET /audit-events?from=2026-07-01&to=2026-07-31&tournamentId=t1
```

---

## Common error codes

| HTTP code | Meaning |
|---|---|
| `400 Bad Request` | Invalid input data |
| `404 Not Found` | Resource doesn't exist (tournament, match, court, rulebook…) |
| `409 Conflict` | Operation not allowed in the current state (tournament paused/inactive, match already inactive, etc.) |
| `500 Internal Server Error` | Unexpected failure generating the fixture (internal random algorithm) |
