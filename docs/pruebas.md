# Pruebas

## Estrategia

Las pruebas siguen la pirámide estándar, con más peso en el dominio y los casos de uso (rápidas y sin infraestructura) y menos en los controladores (integración).

```
          /\
         /  \
        / E2E \      ← Mínimas (smoke tests del API Gateway)
       /--------\
      / Integración\  ← Controllers + repositorio real (Embedded MongoDB)
     /--------------\
    /    Dominio      \  ← Reglas de negocio puras, sin mocks
   /   + Casos de uso  \  ← Casos de uso con repositorio simulado
  /____________________\
```

---

## Pruebas del dominio (sin mocks)

Las reglas de negocio son lógica pura. No necesitan Spring ni MongoDB.

```java
// src/test/java/.../domain/TournamentTest.java
class TournamentTest {

    @Test
    void activate_whenDraft_changesStatusToActive() {
        Tournament tournament = new Tournament("TechCup 2026", /* ... */);
        tournament.activate();
        assertEquals(TournamentStatus.ACTIVO, tournament.getStatus());
    }

    @Test
    void activate_whenAlreadyActive_throwsException() {
        Tournament tournament = buildActiveTournament();
        assertThrows(InvalidTournamentStateException.class, tournament::activate);
    }

    @Test
    void canRegister_whenActive_returnsTrue() {
        Tournament tournament = buildActiveTournament();
        assertTrue(tournament.canRegister());
    }

    @Test
    void canRegister_whenFinished_returnsFalse() {
        Tournament tournament = buildFinishedTournament();
        assertFalse(tournament.canRegister());
    }
}
```

---

## Pruebas de casos de uso (con mocks)

Los casos de uso dependen del puerto de repositorio. Se simula con Mockito.

```java
// src/test/java/.../application/RegisterTeamServiceTest.java
@ExtendWith(MockitoExtension.class)
class RegisterTeamServiceTest {

    @Mock TournamentRepositoryPort tournamentRepo;
    @Mock StoragePort storagePort;

    RegisterTeamService service;

    @BeforeEach
    void setUp() {
        service = new RegisterTeamService(tournamentRepo, storagePort);
    }

    @Test
    void register_whenTournamentActive_createsRegistrationInReview() {
        // Arrange
        String tournamentId = "t01";
        Tournament activeTournament = buildActiveTournament(tournamentId);
        when(tournamentRepo.findById(tournamentId)).thenReturn(Optional.of(activeTournament));
        when(storagePort.upload(any())).thenReturn("https://storage/.../proof.pdf");

        RegisterTeamCommand command = new RegisterTeamCommand(tournamentId, "team01", mockFile());

        // Act
        Registration result = service.register(command);

        // Assert
        assertEquals(RegistrationStatus.EN_REVISION, result.getStatus());
        verify(tournamentRepo).saveRegistration(any(Registration.class));
    }

    @Test
    void register_whenTournamentNotActive_throwsException() {
        Tournament draft = buildDraftTournament("t02");
        when(tournamentRepo.findById("t02")).thenReturn(Optional.of(draft));

        RegisterTeamCommand command = new RegisterTeamCommand("t02", "team01", mockFile());

        assertThrows(TournamentNotActiveException.class, () -> service.register(command));
    }

    @Test
    void register_whenTeamAlreadyRegistered_throwsDuplicateException() {
        Tournament active = buildActiveTournament("t03");
        active.addRegistration(registrationFor("team01"));
        when(tournamentRepo.findById("t03")).thenReturn(Optional.of(active));

        RegisterTeamCommand command = new RegisterTeamCommand("t03", "team01", mockFile());

        assertThrows(DuplicateRegistrationException.class, () -> service.register(command));
    }
}
```

---

## Pruebas del bracket (lógica de generación)

```java
// src/test/java/.../domain/BracketTest.java
class BracketTest {

    @Test
    void generate_with8Teams_creates4InitialMatches() {
        List<String> teamIds = List.of("t1","t2","t3","t4","t5","t6","t7","t8");
        Bracket bracket = Bracket.generate(teamIds);

        assertEquals(4, bracket.getInitialMatches().size());
    }

    @Test
    void generate_withOddTeams_throwsException() {
        List<String> teamIds = List.of("t1","t2","t3");
        assertThrows(IllegalArgumentException.class, () -> Bracket.generate(teamIds));
    }

    @Test
    void generate_initialMatchesAreRandom() {
        // Ejecutar 10 veces y verificar que al menos una ordenación difiere
        List<String> teamIds = IntStream.rangeClosed(1, 8)
            .mapToObj(i -> "t" + i).toList();
        Set<String> firstMatches = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Bracket b = Bracket.generate(teamIds);
            firstMatches.add(b.getInitialMatches().get(0).getTeamAId());
        }
        assertTrue(firstMatches.size() > 1, "La generación no parece aleatoria");
    }
}
```

---

## Ejecutar las pruebas

```bash
# Ejecutar todas las pruebas
./mvnw test

# Ejecutar solo las pruebas del dominio
./mvnw test -Dtest="**/domain/**"

# Ejecutar con reporte de cobertura (JaCoCo)
./mvnw verify

# Ver el reporte HTML
open target/site/jacoco/index.html
```

---

## Cobertura y análisis JaCoCo

<!-- TODO: subir imagen del reporte de cobertura a docs/assets/img/jacoco-coverage.png -->
![Cobertura de pruebas (JaCoCo)](assets/img/jacoco-coverage.png)

---

## CI: las pruebas en el pipeline

El workflow de GitHub Actions ejecuta `mvn clean verify` en cada push y PR. Si alguna prueba falla, el pipeline se pone rojo y **no se puede mergear** a `main`.

```yaml
# .github/workflows/ci.yml
name: CI
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: temurin
      - name: Compilar y probar
        run: ./mvnw clean verify
```
