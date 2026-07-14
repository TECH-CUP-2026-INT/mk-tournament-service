# Arquitectura

## Posición en el ecosistema TechCup

El `mk-tournament-service` es uno de los 14 microservicios que componen la plataforma (ver [Equipo](equipo.md) para el detalle por dominio). Todos se comunican a través del **API Gateway** (`cc-gateway`), que valida el JWT antes de enrutar cada solicitud.

<!-- TODO: subir imagen del diagrama a docs/assets/diagrams/ecosystem-diagram.png -->
![Diagrama del ecosistema TechCup](assets/diagrams/ecosystem-diagram.png)

---

## Arquitectura hexagonal

El servicio implementa la **arquitectura hexagonal** (puertos y adaptadores). El dominio — las reglas del torneo — está en el centro y no depende de ningún framework externo. La base de datos y la API REST son adaptadores intercambiables.

<!-- TODO: subir imagen del diagrama a docs/assets/diagrams/hexagonal-architecture.png -->
![Arquitectura hexagonal](assets/diagrams/hexagonal-architecture.png)

**Regla de oro:** las flechas de dependencia siempre apuntan hacia el centro. `infrastructure → application → domain`. El dominio no importa nada de los otros paquetes.

---

## Estructura de paquetes

```
mk-tournament-service/
├── src/
│   ├── main/
│   │   ├── java/co/edu/escuelaing/techcup/tournament/
│   │   │   ├── aspect/
│   │   │   │   └── AuditEventAspect.java         # AOP: registra auditoría automáticamente
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java            # Spring Security + JWT
│   │   │   ├── controller/
│   │   │   │   ├── api/                           # Interfaces de los controladores
│   │   │   │   └── impl/
│   │   │   │       ├── TournamentController.java  # Controlador principal (todos los endpoints)
│   │   │   │       ├── MatchController.java       # Endpoints de partidos
│   │   │   │       ├── SanctionController.java    # Endpoints de sanciones
│   │   │   │       └── AuditEventController.java  # Endpoints de auditoría
│   │   │   ├── dto/
│   │   │   │   ├── request/                       # DTOs de entrada
│   │   │   │   │   ├── CreateTournamentRequest.java
│   │   │   │   │   ├── EnrollTeamRequest.java
│   │   │   │   │   ├── ScheduleMatchRequest.java
│   │   │   │   │   └── ...
│   │   │   │   └── response/                      # DTOs de salida
│   │   │   │       ├── TournamentResponse.java
│   │   │   │       ├── EnrollmentResponse.java
│   │   │   │       ├── MatchupResponse.java
│   │   │   │       └── ...
│   │   │   ├── entity/
│   │   │   │   └── document/                      # Documentos MongoDB
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
│   │   │   │   ├── GlobalExceptionHandler.java    # Manejo centralizado de errores
│   │   │   │   └── *.java                         # Excepciones de negocio
│   │   │   ├── mapper/                            # Conversión entre capas
│   │   │   ├── repository/                        # Repositorios MongoDB
│   │   │   └── service/                           # Lógica de negocio y casos de uso
│   │   │       ├── Tournament.java                # Modelo de dominio (reglas del torneo)
│   │   │       ├── TournamentParticipant.java
│   │   │       ├── TournamentStatus.java           # Enum de estados
│   │   │       ├── TournamentFormat.java
│   │   │       └── impl/                          # Implementaciones de casos de uso
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
│       │   ├── service/                   # Pruebas del dominio (TournamentTest, CourtTest…)
│       │   ├── service/impl/              # Pruebas de servicios (con Mockito)
│       │   ├── bdd/                       # Pruebas BDD con Cucumber
│       │   ├── mapper/                    # Pruebas de mappers
│       │   └── repository/adapter/        # Pruebas de adaptadores
│       └── resources/features/            # Archivos .feature (Gherkin)
│           ├── delete_tournament.feature
│           └── enroll_team_in_tournament.feature
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## Modelo de dominio

<!-- TODO: subir imagen del diagrama a docs/assets/diagrams/domain-model.png -->
![Modelo de dominio](assets/diagrams/domain-model.png)

---

## Flujo de una inscripción

<!-- TODO: subir imagen del diagrama a docs/assets/diagrams/enrollment-sequence.png -->
![Flujo de una inscripción](assets/diagrams/enrollment-sequence.png)

---

## Seguridad

!!! warning "Estado actual"
    Todavía no existe un Servicio de Identidad con JWT/roles en la plataforma. Hoy **todos** los endpoints del servicio están abiertos (`permitAll()`). El diseño por rol (Organizador, Capitán, Árbitro, Admin) es la intención de negocio documentada en [API REST](api.md), pero no está forzada en código.

```java
// config/SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Pendiente del futuro Servicio de Identidad (JWT + roles)
                        .requestMatchers("/tournaments/**", "/matches/**", "/sanctions/**",
                                "/audit-events/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
```
