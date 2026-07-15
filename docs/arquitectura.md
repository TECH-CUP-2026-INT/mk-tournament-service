# Architecture

## Position within the TechCup ecosystem

`mk-tournament-service` is one of the 14 microservices that make up the platform (see [Team](equipo.md) for the breakdown by domain). All of them communicate through the **API Gateway** (`cc-gateway`), which validates the JWT before routing each request.

---

## Hexagonal architecture

The service implements **hexagonal architecture** (ports and adapters). The domain — the tournament rules — sits at the center and does not depend on any external framework. The database and the REST API are interchangeable adapters.

**Golden rule:** dependency arrows always point toward the center. `infrastructure → application → domain`. The domain imports nothing from the other packages.

---

## Package structure

```
mk-tournament-service/
├── src/
│   ├── main/
│   │   ├── java/co/edu/escuelaing/techcup/tournament/
│   │   │   ├── aspect/
│   │   │   │   └── AuditEventAspect.java         # AOP: captures audit events automatically
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java            # Spring Security + JWT
│   │   │   ├── controller/
│   │   │   │   ├── api/                           # Controller interfaces
│   │   │   │   └── impl/
│   │   │   │       ├── TournamentController.java  # Main controller (most endpoints)
│   │   │   │       ├── MatchController.java       # Match endpoints
│   │   │   │       ├── SanctionController.java    # Sanction endpoints
│   │   │   │       └── AuditEventController.java  # Audit endpoints
│   │   │   ├── dto/
│   │   │   │   ├── request/                       # Inbound DTOs
│   │   │   │   │   ├── CreateTournamentRequest.java
│   │   │   │   │   ├── EnrollTeamRequest.java
│   │   │   │   │   ├── ScheduleMatchRequest.java
│   │   │   │   │   └── ...
│   │   │   │   └── response/                      # Outbound DTOs
│   │   │   │       ├── TournamentResponse.java
│   │   │   │       ├── EnrollmentResponse.java
│   │   │   │       ├── MatchupResponse.java
│   │   │   │       └── ...
│   │   │   ├── entity/
│   │   │   │   └── document/                      # MongoDB documents
│   │   │   │       ├── TournamentDocument.java
│   │   │   │       ├── EnrollmentDocument.java
│   │   │   │       ├── MatchDocument.java
│   │   │   │       ├── CourtDocument.java
│   │   │   │       ├── ScheduledMatchDocument.java
│   │   │   │       ├── PlayerSanctionDocument.java
│   │   │   │       ├── TeamRegistrationDocument.java
│   │   │   │       ├── TournamentParticipantDocument.java
│   │   │   │       └── AuditEventDocument.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java    # Centralized error handling
│   │   │   │   └── *.java                         # Business exceptions
│   │   │   ├── mapper/                            # Conversion between layers
│   │   │   ├── repository/                        # MongoDB repositories
│   │   │   └── service/                           # Business logic and use cases
│   │   │       ├── Tournament.java                # Domain model (tournament rules)
│   │   │       ├── TournamentParticipant.java
│   │   │       ├── TournamentStatus.java           # Status enum
│   │   │       ├── TournamentFormat.java
│   │   │       └── impl/                          # Use case implementations
│   │   │           ├── CreateTournamentService.java
│   │   │           ├── EnrollTeamInTournamentService.java
│   │   │           ├── FinalizeTournamentService.java
│   │   │           ├── ScheduleMatchService.java
│   │   │           ├── AssignChampionService.java
│   │   │           └── ...
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/.../tournament/
│       │   ├── service/                   # Domain tests (TournamentTest, CourtTest…)
│       │   ├── service/impl/              # Service tests (with Mockito)
│       │   ├── bdd/                       # BDD tests with Cucumber
│       │   ├── mapper/                    # Mapper tests
│       │   └── repository/adapter/        # Adapter tests
│       └── resources/features/            # .feature files (Gherkin)
│           ├── delete_tournament.feature
│           └── enroll_team_in_tournament.feature
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## Security

!!! warning "Current state"
    There is no Identity Service with JWT/roles in the platform yet. Today **every** endpoint in the service is open (`permitAll()`). The role-based design (Organizer, Captain, Referee, Admin) is the documented business intent, described in [REST API](api.md), but it isn't enforced in code.

```java
// config/SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Pending the future Identity Service (JWT + roles)
                        .requestMatchers("/tournaments/**", "/matches/**", "/sanctions/**",
                                "/audit-events/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
```
