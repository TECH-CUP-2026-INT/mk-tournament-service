# API REST

Base URL: `http://localhost:8080`

!!! warning "Control de acceso"
    Todos los endpoints están actualmente **abiertos** (`permitAll()` en `SecurityConfig`). No existe todavía un Servicio de Identidad con JWT/roles en la plataforma. La columna **Rol previsto** describe la intención de negocio, no una restricción real. Ningún endpoint debería quedar público así en producción.

---

## Gestión de torneos — `/tournaments`

Controlador: `TournamentController`.

| Método | Endpoint | Descripción | Rol previsto |
|---|---|---|---|
| `POST` | `/tournaments` | Crear torneo | Organizador |
| `PATCH` | `/tournaments/{id}` | Editar cualquier campo del torneo | Organizador |
| `PATCH` | `/tournaments/{id}/finalize` | Finalizar torneo (pasa a histórico) | Organizador |
| `PATCH` | `/tournaments/{id}/prepare` | Iniciar preparación del torneo (genera emparejamientos) | Organizador |
| `PATCH` | `/tournaments/{id}/pause` | Pausar / reanudar torneo (`action: PAUSE \| RESUME`) | Organizador |
| `PATCH` | `/tournaments/{id}/inactivate` | Inactivar / reactivar torneo (`action: INACTIVATE \| REACTIVATE`) | Organizador |
| `DELETE` | `/tournaments/{id}` | Eliminar torneo (solo si está Finalizado) | Organizador |
| `GET` | `/tournaments/{tournamentId}/preparation` | Consultar estado de preparación | Autenticado |

### Crear torneo

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

**Respuesta 201:** `TournamentResponse` con el torneo creado en estado `ACTIVE`.

### Pausar / reanudar torneo

```http
PATCH /tournaments/{id}/pause
Content-Type: application/json
```

```json
{ "action": "PAUSE" }
```

### Inactivar / reactivar torneo

```http
PATCH /tournaments/{id}/inactivate
Content-Type: application/json
```

```json
{ "action": "INACTIVATE" }
```

---

## Inscripciones — `/tournaments/{tournamentId}/enrollments`

| Método | Endpoint | Descripción | Rol previsto |
|---|---|---|---|
| `POST` | `/tournaments/{tournamentId}/enrollments` | Inscribir equipo | Capitán |
| `GET` | `/tournaments/{tournamentId}/enrollments` | Consultar equipos inscritos y reservados | Autenticado |

### Inscribir equipo

```http
POST /tournaments/{tournamentId}/enrollments
Content-Type: application/json
```

```json
{ "teamId": "team_xyz789" }
```

**Respuesta 201:** `EnrollmentResponse` — `enrollmentId`, `status` (`PENDING_PAYMENT`, …), `reservationExpiresAt`.

---

## Equipos y usuarios en un torneo

| Método | Endpoint | Descripción | Rol previsto |
|---|---|---|---|
| `GET` | `/tournaments/{tournamentId}/teams` | Listar equipos registrados | Autenticado |
| `PATCH` | `/tournaments/{tournamentId}/teams/{teamId}/disqualify` | Descalificar equipo | Organizador |
| `PATCH` | `/tournaments/{tournamentId}/teams/{teamId}/inactivate` | Inactivar equipo en el torneo | Organizador |
| `PATCH` | `/tournaments/{tournamentId}/users/{userId}/inactivate` | Inactivar usuario en el torneo | Organizador |

---

## Llaves y partidos

| Método | Endpoint | Descripción | Rol previsto |
|---|---|---|---|
| `GET` | `/tournaments/{tournamentId}/matchups` | Ver llaves / emparejamientos con resultados | Público |
| `GET` | `/tournaments/matches/{matchId}/court` | Ver cancha asignada a un partido | Autenticado |
| `POST` | `/tournaments/{tournamentId}/matches/{matchId}/champion` | Asignar campeón (partido final finalizado) | Organizador |
| `GET` | `/tournaments/{tournamentId}/champion` | Consultar campeón del torneo | Público |

---

## Partidos — `/matches`

Controlador: `MatchController`.

| Método | Endpoint | Descripción | Rol previsto |
|---|---|---|---|
| `POST` | `/matches` | Programar partido (emparejamiento + cancha + árbitro + fecha/hora) | Organizador |
| `PATCH` | `/matches/{matchId}/activation` | Inactivar / reactivar un partido (`action: INACTIVATE \| REACTIVATE`) | Organizador |

### Programar partido

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

### Inactivar / reactivar partido

```http
PATCH /matches/{matchId}/activation
Content-Type: application/json
```

```json
{ "action": "INACTIVATE" }
```

**Respuesta 200:**

```json
{
  "matchId": "m01",
  "active": false,
  "message": "El partido fue inactivado correctamente"
}
```

Un partido inactivo **conserva los datos ya registrados** (marcador, estado) pero bloquea `finish()`, el registro del ganador de penales y la marca de no-show. Tarjetas, sustituciones y manejo del reloj no se implementan en este servicio — son responsabilidad del futuro **Servicio de Partidos**, que debe consultar este estado antes de aceptar esos eventos.

---

## Canchas

| Método | Endpoint | Descripción | Rol previsto |
|---|---|---|---|
| `POST` | `/tournaments/{tournamentId}/courts` | Registrar cancha (multipart: `section`, `description`, `image`) | Organizador |

---

## Reglamento

| Método | Endpoint | Descripción | Rol previsto |
|---|---|---|---|
| `POST` | `/tournaments/{tournamentId}/rulebook` | Adjuntar reglamento (PDF, multipart `file`) | Organizador |
| `GET` | `/tournaments/{tournamentId}/rulebook` | Consultar / descargar reglamento | Público |

---

## Histórico de torneos

| Método | Endpoint | Descripción | Rol previsto |
|---|---|---|---|
| `GET` | `/tournaments/history` | Listar todos los torneos finalizados | Público |
| `GET` | `/tournaments/history/{tournamentId}` | Detalle de un torneo histórico | Público |

---

## Sanciones — `/sanctions`

Controlador: `SanctionController`.

| Método | Endpoint | Descripción | Rol previsto |
|---|---|---|---|
| `POST` | `/sanctions` | Aplicar sanción a un jugador | Árbitro / Organizador |
| `GET` | `/sanctions/{playerId}` | Ver sanciones activas de un jugador | Autenticado |
| `POST` | `/sanctions/match-finished` | Punto de integración: descuenta partidos de suspensión cuando un partido finaliza | *(pendiente — sin disparador automático aún)* |

---

## Auditoría — `/audit-events`

Controlador: `AuditEventController`. Todas las acciones exitosas de los servicios de aplicación quedan registradas automáticamente vía un aspecto AOP (`AuditEventAspect`).

| Método | Endpoint | Descripción | Rol previsto |
|---|---|---|---|
| `GET` | `/audit-events` | Consultar eventos, con filtros opcionales combinados por AND: `from`, `to`, `eventType`, `tournamentId` | Admin / Organizador |

```http
GET /audit-events?from=2026-07-01&to=2026-07-31&tournamentId=t1
```

---

## Códigos de error comunes

| Código HTTP | Significado |
|---|---|
| `400 Bad Request` | Datos de entrada inválidos |
| `404 Not Found` | Recurso no existe (torneo, partido, cancha, reglamento…) |
| `409 Conflict` | Operación no permitida en el estado actual (torneo pausado/inactivo, partido ya inactivo, etc.) |
| `500 Internal Server Error` | Falla inesperada al generar el fixture (algoritmo aleatorio interno) |
