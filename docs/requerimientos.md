# Requerimientos

## Contexto

**Dominio:** D2 — Torneo y Competencia
**Servicio:** Servicio de Torneos (`mk-tournament-service`)

## Requerimientos Funcionales

25 requerimientos funcionales (TC-29 a TC-54, sin TC-52 — pertenece al Servicio de Inscripción del torneo).

| ID | Requirement | Description |
|---|---|---|
| TC-29 | Tournament creation | Form: name, type (Normal/Lightning), format (Brackets/Groups/League), minimum 3 teams, number of teams, cost, registration deadline, schedule of dates (or start/end time if Lightning). The tournament is created in Active status. |
| TC-30 | Register tournament rulebook | Upload the official rulebook PDF (max. 10 MB); linked to the tournament. |
| TC-31 | Register tournament courts | Name (required), image (optional), description (optional), and campus map coordinates (required). |
| TC-32 | Generate matchups | Via external API or randomly depending on the format: Brackets (initial random, then by result), Groups (random within groups, cross by points), League (round robin). |
| TC-33 | Schedule matches | CREATE the match: assigns the matchup, the court, the referee, the date and the time. Creates a new match document in the database. |
| TC-34 | View the internal rulebook | Any authenticated user can view or download the rulebook PDF. |
| TC-35 | View interactive court map | Shows each court on the map with its assigned match and status (Scheduled/In Progress/Finished); status with color + icon/text. |
| TC-36 | View history of finished tournaments | Read-only; accessible without authentication. |
| TC-37 | View all teams registered in a tournament | List of teams with name and logo; visible to all authenticated users. |
| TC-38 | View matchups | Visual view of the bracket/groups/league with current results; future slots shown as "To be defined". |
| TC-39 | View match courts | Shows the court assigned to a match with name, image and location. |
| TC-40 | Consult Tournament Service events | Audit log; accessible by Admin and Organizer. |
| TC-41 | Edit tournament | Allows modifying any field from tournament creation; blocked if the tournament is Finished. |
| TC-42 | Pause tournament | Suspends event registration but all data remains queryable; can be resumed. |
| TC-43 | Inactivate tournament | Blocks ALL tournament functionality including queries; can be reactivated. |
| TC-44 | Inactivate a team in the tournament | The inactivated team receives no scheduling or points; temporary administrative measure. |
| TC-45 | Inactivate user (tournament level) | The inactivated user cannot be included in lineups or accumulate statistics in that tournament. |
| TC-46 | Inactivate match | The inactivated match does not allow recording score, goals, fouls, substitutions or clock. |
| TC-47 | Sanction player (within the tournament) | Red card = 1 match; 2 yellow cards in different matches = 1 match; 2 yellow cards in the same match = red; for conduct, the Organizer defines the number of matches suspended. |
| TC-48 | Team disqualification | Marks the team as disqualified (stays in records); no longer included in future matchups; previous statistics still count. |
| TC-49 | Finalize tournament (save to history) | Only if the tournament is In Progress and the end date has been reached; moves to Finished and becomes read-only. |
| TC-50 | Assign the champion team to a tournament | Triggered when the final match finishes; if tied, the champion is defined by a penalty shootout. |
| TC-51 | Delete tournament | Can only be deleted if the tournament is in Finished status; permanent deletion. |
| TC-53 | Enroll team in tournament | The Captain enrolls their team; requires 7-12 players and the tournament in Active status; the enrollment is created in Pending Payment status. |
| TC-54 | View enrolled teams | *(No description captured)* |

### Detailed example — TC-46: Inactivate match

```
ID:             TC-46
Name:           Inactivate match
Actor:          Organizer
Domain:         D2 - Tournament and Competition

Preconditions:
  - The match exists and belongs to a tournament
  - The match is currently active

Main flow:
  1. The organizer selects a match and requests to inactivate it
  2. The system blocks referee-event registration on that match:
     score/result, penalty shootout winner, no-show
  3. Previously recorded data on the match (score, status) is preserved
  4. The organizer can reactivate the match later, restoring the
     ability to register events on it

Alternate flow:
  2a. The match is already inactive → the system rejects the request
      with a conflict error
  4a. The match is already active → reactivation is rejected the same way

Postconditions:
  - The match keeps an `active` flag independent from its `status`
  - Cards, substitutions and clock management are NOT covered by this
    requirement in the Tournament Service — they belong to the future
    Match Service, which must consult this flag before accepting those
    events

Acceptance criteria:
  - A match cannot be inactivated twice in a row without reactivating first
  - finish(), recordPenaltyShootoutWinner() and markAsNoShow() are blocked
    while the match is inactive
  - Reactivating an already-active match is rejected
```

---

## Non-Functional Requirements

| ID | Category | Requirement | How it is measured |
|---|---|---|---|
| RNF-01 | Performance | The tournament query endpoint responds in under 500 ms | Average response time under normal load |
| RNF-02 | Performance | Bracket generation completes in under 2 seconds | Performance test with 32 teams |
| RNF-03 | Security | All endpoints (except public queries) require a valid JWT | Review of Spring Security config + tests |
| RNF-04 | Security | Enrollment data (proof of payment) is stored encrypted at rest | Review of MongoDB configuration |
| RNF-05 | Availability | The service is available 99% of the time during the semester | Monthly monitoring in the cloud |
| RNF-06 | Maintainability | The code follows a layered structure: `controller → service → repository`, with no inverted dependencies | Code review + isolated build tests |
| RNF-07 | Portability | The service runs in Docker in any environment with Java 21 | Test in a clean environment with `docker compose up` |
| RNF-08 | Scalability | The service can be deployed with multiple instances without shared in-memory state | Stateless server design; session in JWT |
| RNF-09 | Traceability | Every action on a tournament (create, activate, enroll, approve) generates an audit record | Log review + audit collection in MongoDB |

---

## Estados del Torneo

<!-- TODO: subir imagen del diagrama a docs/assets/diagrams/tournament-states.png -->
![Estados del torneo](assets/diagrams/tournament-states.png)

## Estados de la Inscripción

<!-- TODO: subir imagen del diagrama a docs/assets/diagrams/enrollment-states.png -->
![Estados de la inscripción](assets/diagrams/enrollment-states.png)
