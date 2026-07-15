# REST API

Base URL: `http://localhost:8080`

!!! warning "Access control"
    All endpoints are currently **open** (`permitAll()` in `SecurityConfig`). There is no Identity Service with JWT/roles in the platform yet. The **Intended role** column describes the business intent, not an actual restriction. No endpoint should be left public like this in production.

---

## Tournament management — `/tournaments`

Controller: `TournamentController`.

| Method | Endpoint | Description | Intended role |
|---|---|---|---|
| `POST` | `/tournaments` | Create tournament | Organizer |
| `PATCH` | `/tournaments/{id}` | Edit any tournament field | Organizer |
| `PATCH` | `/tournaments/{id}/finalize` | Finalize tournament (moves to history) | Organizer |
| `PATCH` | `/tournaments/{id}/prepare` | Start tournament preparation (generates matchups) | Organizer |
| `PATCH` | `/tournaments/{id}/pause` | Pause / resume tournament (`action: PAUSE \| RESUME`) | Organizer |
| `PATCH` | `/tournaments/{id}/inactivate` | Inactivate / reactivate tournament (`action: INACTIVATE \| REACTIVATE`) | Organizer |
| `DELETE` | `/tournaments/{id}` | Delete tournament (only if Finished) | Organizer |
| `GET` | `/tournaments/{tournamentId}/preparation` | Check preparation readiness | Authenticated |

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

**201 response:** `TournamentResponse` with the tournament created in `ACTIVE` status.

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
| `POST` | `/sanctions/match-finished` | Integration point: serves one match of suspension when a match finishes | *(pending — no automatic trigger yet)* |

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
