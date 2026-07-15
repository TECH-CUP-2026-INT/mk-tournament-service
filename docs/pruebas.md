# Tests

## Strategy

Tests follow the standard pyramid, weighted toward the domain and use cases (fast, no infrastructure) and lighter on controllers (integration).

```
          /\
         /  \
        / E2E \      ← Minimal (API Gateway smoke tests)
       /--------\
      / Integration\  ← Controllers + real repository (Embedded MongoDB)
     /--------------\
    /     Domain      \  ← Pure business rules, no mocks
   /   + Use cases     \  ← Use cases with a simulated repository
  /____________________\
```

---

## Domain tests (no mocks)

Business rules are pure logic. They don't need Spring or MongoDB.

```java
// src/test/java/.../domain/TournamentTest.java
class TournamentTest {

    @Test
    void activate_whenDraft_changesStatusToActive() {
        Tournament tournament = new Tournament("TechCup 2026", /* ... */);
        tournament.activate();
        assertEquals(TournamentStatus.ACTIVE, tournament.getStatus());
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

## Use case tests (with mocks)

Use cases depend on the repository port. It's simulated with Mockito.

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
        assertEquals(RegistrationStatus.UNDER_REVIEW, result.getStatus());
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

## Bracket tests (generation logic)

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
        // Run 10 times and check that at least one ordering differs
        List<String> teamIds = IntStream.rangeClosed(1, 8)
            .mapToObj(i -> "t" + i).toList();
        Set<String> firstMatches = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Bracket b = Bracket.generate(teamIds);
            firstMatches.add(b.getInitialMatches().get(0).getTeamAId());
        }
        assertTrue(firstMatches.size() > 1, "Generation doesn't look random");
    }
}
```

---

## Running the tests

```bash
# Run all tests
./mvnw test

# Run only domain tests
./mvnw test -Dtest="**/domain/**"

# Run with coverage report (JaCoCo)
./mvnw verify

# View the HTML report
open target/site/jacoco/index.html
```

---

## CI: tests in the pipeline

The GitHub Actions workflow runs `mvn clean verify` on every push and PR. If any test fails, the pipeline turns red and **merging to `main` is blocked**.

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
      - name: Build and test
        run: ./mvnw clean verify
```
