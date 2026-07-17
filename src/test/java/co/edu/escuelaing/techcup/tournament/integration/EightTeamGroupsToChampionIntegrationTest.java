package co.edu.escuelaing.techcup.tournament.integration;

import co.edu.escuelaing.techcup.tournament.application.usecase.ActivateTournamentService;
import co.edu.escuelaing.techcup.tournament.application.usecase.BeginTournamentService;
import co.edu.escuelaing.techcup.tournament.application.usecase.CreateTournamentService;
import co.edu.escuelaing.techcup.tournament.application.usecase.ProcessMatchResultService;
import co.edu.escuelaing.techcup.tournament.application.usecase.StartTournamentPreparationService;
import co.edu.escuelaing.techcup.tournament.domain.model.BracketNode;
import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.domain.model.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Round;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.CreateTournamentUseCase.CreateTournamentCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ProcessMatchResultUseCase.ProcessMatchResultCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.FixtureGenerationPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter.RandomFixtureGenerationAdapter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Extremo a extremo del flujo "mundial" (8 equipos, 2 grupos) enteramente vía
 * casos de uso reales (sin Spring context: este repo no tiene Mongo embebido,
 * ver ServiceTournamentApplicationTests#contextLoads) y ProcessMatchResult por
 * el mismo camino que usaría /sim o el listener de RabbitMQ.
 * <p>
 * "aprobar 8" se simula fijando teams()/enrollments() directamente: no existe
 * en este repo un caso de uso único de "aprobar equipo" (SyncEnrollmentStatusService
 * solo sincroniza el estado de pago RESERVED -&gt; ENROLLED/REJECTED), así que
 * se deja directamente el resultado esperado de esa aprobación.
 */
class EightTeamGroupsToChampionIntegrationTest {

    private final Map<UUID, Tournament> store = new HashMap<>();
    private final TournamentRepositoryPort repository = new InMemoryTournamentRepositoryPort(store);

    private final CreateTournamentService createTournamentService = new CreateTournamentService(repository);
    private final ActivateTournamentService activateTournamentService = new ActivateTournamentService(repository);
    // Constructor con Random sembrado es package-private (mismo paquete que su test unitario);
    // este test no necesita determinismo de sorteo: la asignación a grupos que resulte se
    // descubre en rankTeamsByGroup() y se ordena arbitraria pero totalmente, sin importar qué
    // equipo cayó en qué grupo.
    private final FixtureGenerationPort fixturePort = new RandomFixtureGenerationAdapter();
    private final StartTournamentPreparationService startPreparationService =
            new StartTournamentPreparationService(repository, fixturePort);
    private final BeginTournamentService beginTournamentService = new BeginTournamentService(repository);
    private final ProcessMatchResultService processMatchResultService = new ProcessMatchResultService(
            repository, () -> { }, tournamentId -> { }, tournamentId -> { });

    @Test
    void ochoEquipos_dosGrupos_generaLlaveYDeterminaCampeonYSubcampeon() {
        Tournament created = createTournamentService.create(new CreateTournamentCommand(
                "Copa Mundial TechCup", TournamentType.NORMAL, TournamentFormat.GROUPS, 8, BigDecimal.ZERO,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(30), LocalDate.now().plusDays(5), null, null));
        UUID tournamentId = created.getId();
        assertEquals(TournamentStatus.DRAFT, created.getStatus());

        activateTournamentService.activate(tournamentId);
        assertEquals(TournamentStatus.ACTIVE, repository.findById(tournamentId).orElseThrow().getStatus());

        approveEightTeams(tournamentId);

        Tournament prepared = startPreparationService.startPreparation(tournamentId);
        assertEquals(TournamentStatus.IN_PREPARATION, prepared.getStatus());
        assertEquals(12, prepared.getMatches().size());

        Tournament started = beginTournamentService.begin(tournamentId);
        assertEquals(TournamentStatus.IN_PROGRESS, started.getStatus());

        Map<String, List<UUID>> rankByGroup = rankTeamsByGroup(started.getMatches());
        for (Match match : new ArrayList<>(started.getMatches())) {
            List<UUID> ranking = rankByGroup.get(match.getGroupName());
            boolean homeWins = ranking.indexOf(match.getHomeTeamId()) < ranking.indexOf(match.getAwayTeamId());
            int homeScore = homeWins ? 2 : 0;
            int awayScore = homeWins ? 0 : 2;
            UUID winnerId = homeWins ? match.getHomeTeamId() : match.getAwayTeamId();

            processMatchResultService.process(new ProcessMatchResultCommand(
                    match.getMatchId(), tournamentId, MatchPhase.GRUPOS, homeScore, awayScore, winnerId));
        }

        Tournament afterGroups = repository.findById(tournamentId).orElseThrow();
        assertEquals(3, afterGroups.getBracketNodes().size(), "2 semis + 1 final");
        assertEquals(TournamentStatus.IN_PROGRESS, afterGroups.getStatus());

        List<BracketNode> semis = afterGroups.getBracketNodes().stream()
                .filter(n -> n.getRound() == Round.SEMIFINAL).toList();
        assertEquals(2, semis.size());
        for (BracketNode semi : semis) {
            resolveElimination(tournamentId, semi.getMatchId(), semi.getSlotA());
        }

        Tournament beforeFinal = repository.findById(tournamentId).orElseThrow();
        BracketNode finalNode = beforeFinal.getBracketNodes().stream()
                .filter(n -> n.getRound() == Round.FINAL).findFirst().orElseThrow();
        assertNotNull(finalNode.getSlotA());
        assertNotNull(finalNode.getSlotB());
        UUID championId = finalNode.getSlotA();
        UUID runnerUpId = finalNode.getSlotB();

        resolveElimination(tournamentId, finalNode.getMatchId(), championId);

        Tournament finished = repository.findById(tournamentId).orElseThrow();
        assertEquals(TournamentStatus.FINISHED, finished.getStatus());
        assertEquals(championId, finished.getChampionTeamId());
        assertEquals(runnerUpId, finished.getRunnerUpTeamId());
        assertNotEquals(finished.getChampionTeamId(), finished.getRunnerUpTeamId());
    }

    private void resolveElimination(UUID tournamentId, UUID matchId, UUID winnerId) {
        processMatchResultService.process(new ProcessMatchResultCommand(
                matchId, tournamentId, MatchPhase.ELIMINATORIA, 2, 0, winnerId));
    }

    private void approveEightTeams(UUID tournamentId) {
        Tournament tournament = repository.findById(tournamentId).orElseThrow();
        List<TeamRegistration> teams = new ArrayList<>();
        List<Enrollment> enrollments = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            UUID teamId = UUID.randomUUID();
            teams.add(new TeamRegistration(teamId, "Equipo " + i, RegistrationStatus.APPROVED));
            enrollments.add(new Enrollment(teamId, "Equipo " + i, EnrollmentStatus.ENROLLED));
        }
        tournament.setTeams(teams);
        tournament.setEnrollments(enrollments);
        repository.save(tournament);
    }

    /** Orden arbitrario pero total por equipo dentro de cada grupo: el de menor índice siempre gana. */
    private Map<String, List<UUID>> rankTeamsByGroup(List<Match> matches) {
        Map<String, List<UUID>> byGroup = new LinkedHashMap<>();
        for (Match m : matches) {
            List<UUID> teams = byGroup.computeIfAbsent(m.getGroupName(), g -> new ArrayList<>());
            if (!teams.contains(m.getHomeTeamId())) teams.add(m.getHomeTeamId());
            if (!teams.contains(m.getAwayTeamId())) teams.add(m.getAwayTeamId());
        }
        return byGroup;
    }

    /** Fake mínimo en memoria: este repo no tiene Mongo embebido para tests con Spring context real. */
    private static final class InMemoryTournamentRepositoryPort implements TournamentRepositoryPort {
        private final Map<UUID, Tournament> store;

        private InMemoryTournamentRepositoryPort(Map<UUID, Tournament> store) {
            this.store = store;
        }

        @Override
        public Tournament save(Tournament tournament) {
            store.put(tournament.getId(), tournament);
            return tournament;
        }

        @Override
        public Optional<Tournament> findById(UUID id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public void deleteById(UUID id) {
            store.remove(id);
        }

        @Override
        public List<Tournament> findAllByStatus(TournamentStatus status) {
            return store.values().stream().filter(t -> t.getStatus() == status).toList();
        }

        @Override
        public Optional<Tournament> findByIdAndStatus(UUID id, TournamentStatus status) {
            return findById(id).filter(t -> t.getStatus() == status);
        }

        @Override
        public Optional<Tournament> findByMatchId(UUID matchId) {
            return store.values().stream()
                    .filter(t -> t.getMatches().stream().anyMatch(m -> m.getMatchId().equals(matchId)))
                    .findFirst();
        }

        @Override
        public List<Tournament> findAllWithReservedEnrollments() {
            return List.of();
        }

        @Override
        public boolean existsActiveEnrollmentForTeam(UUID teamId) {
            return false;
        }
    }
}
