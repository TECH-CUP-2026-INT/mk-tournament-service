package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.PreparationResult;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.RemovalReason;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

import co.edu.escuelaing.techcup.tournament.domain.exception.TeamRemovalNotAllowedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TournamentDomainTest {

    private Tournament buildTournament(TournamentStatus status, List<TeamRegistration> teams) {
        return Tournament.builder()
                .id(UUID.randomUUID()).name("TechCup").numberOfTeams(4).cost(BigDecimal.ZERO)
                .startDate(LocalDate.now().plusDays(2)).endDate(LocalDate.now().plusDays(10))
                .registrationDeadline(LocalDate.now())
                .status(status).teams(new ArrayList<>(teams)).matches(new ArrayList<>())
                .reconstruct();
    }

    // --- Preparación ---

    @Test
    void preparation_menosDeTreeEquiposAprobados_retornaIncompleto() {
        Tournament t = buildTournament(TournamentStatus.DRAFT, List.of(
                new TeamRegistration(UUID.randomUUID(), "Equipo 1", RegistrationStatus.APPROVED),
                new TeamRegistration(UUID.randomUUID(), "Equipo 2", RegistrationStatus.APPROVED)
        ));
        PreparationResult result = t.checkPreparation();
        assertFalse(result.isReadyToActivate());
        assertTrue(result.getMissingRequirements().stream().anyMatch(m -> m.contains("faltan")));
    }

    @Test
    void preparation_tresEquiposAprobadosYFechasValidas_retornaCompleto() {
        Tournament t = buildTournament(TournamentStatus.DRAFT, List.of(
                new TeamRegistration(UUID.randomUUID(), "Equipo 1", RegistrationStatus.APPROVED),
                new TeamRegistration(UUID.randomUUID(), "Equipo 2", RegistrationStatus.APPROVED),
                new TeamRegistration(UUID.randomUUID(), "Equipo 3", RegistrationStatus.APPROVED)
        ));
        PreparationResult result = t.checkPreparation();
        assertTrue(result.isReadyToActivate());
        assertTrue(result.getMissingRequirements().isEmpty());
    }

    @Test
    void preparation_sinFechas_retornaIncompleto() {
        Tournament t = Tournament.builder()
                .id(UUID.randomUUID()).name("TechCup").numberOfTeams(4).cost(BigDecimal.ZERO)
                .status(TournamentStatus.DRAFT)
                .teams(new ArrayList<>(List.of(
                        new TeamRegistration(UUID.randomUUID(), "E1", RegistrationStatus.APPROVED),
                        new TeamRegistration(UUID.randomUUID(), "E2", RegistrationStatus.APPROVED),
                        new TeamRegistration(UUID.randomUUID(), "E3", RegistrationStatus.APPROVED)
                )))
                .matches(new ArrayList<>())
                .reconstruct();
        PreparationResult result = t.checkPreparation();
        assertFalse(result.isReadyToActivate());
        assertTrue(result.getMissingRequirements().stream().anyMatch(m -> m.contains("Fechas")));
    }

    // --- Eliminación de equipo ---

    @Test
    void removeTeam_torneoActivo_eliminaEquipoYMarcaPartidosPendientes() {
        UUID team1 = UUID.randomUUID();
        UUID team2 = UUID.randomUUID();
        Tournament t = buildTournament(TournamentStatus.ACTIVE, List.of(
                new TeamRegistration(team1, "Equipo 1", RegistrationStatus.APPROVED),
                new TeamRegistration(team2, "Equipo 2", RegistrationStatus.APPROVED)
        ));
        Match pendingMatch = new Match(UUID.randomUUID(), team1, team2, MatchStatus.PENDING);
        t.setMatches(new ArrayList<>(List.of(pendingMatch)));

        List<Match> affected = t.removeTeam(team1, RemovalReason.DIRECT_DISQUALIFICATION);

        assertEquals(1, t.getTeams().size());
        assertEquals(1, affected.size());
        assertEquals(MatchStatus.FINISHED_NO_SHOW, affected.get(0).getStatus());
    }

    @Test
    void removeTeam_torneoEnDraft_lanza409() {
        UUID team1 = UUID.randomUUID();
        Tournament t = buildTournament(TournamentStatus.DRAFT, List.of(
                new TeamRegistration(team1, "Equipo 1", RegistrationStatus.APPROVED)
        ));
        assertThrows(TeamRemovalNotAllowedException.class,
                () -> t.removeTeam(team1, RemovalReason.DISCIPLINARY_POINTS));
    }

    @Test
    void removeTeam_equipoNoInscrito_lanzaExcepcion() {
        UUID team1 = UUID.randomUUID();
        UUID otherTeam = UUID.randomUUID();
        Tournament t = buildTournament(TournamentStatus.IN_PROGRESS, List.of(
                new TeamRegistration(team1, "Equipo 1", RegistrationStatus.APPROVED)
        ));
        assertThrows(TeamRemovalNotAllowedException.class,
                () -> t.removeTeam(otherTeam, RemovalReason.DIRECT_DISQUALIFICATION));
    }
}
