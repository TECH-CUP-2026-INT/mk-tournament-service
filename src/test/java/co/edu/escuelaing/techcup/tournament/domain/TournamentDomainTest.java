package co.edu.escuelaing.techcup.tournament.domain;

import co.edu.escuelaing.techcup.tournament.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TournamentDomainTest {

    private Tournament buildTournament(TournamentStatus status, List<TeamRegistration> teams) {
        Tournament t = new Tournament("t1", "TechCup", LocalDate.now(), LocalDate.now().plusDays(10), EliminationType.DIRECT);
        t.setStatus(status);
        t.setTeams(new java.util.ArrayList<>(teams));
        return t;
    }

    // --- Preparación ---

    @Test
    void preparation_menosDeTreeEquiposAprobados_retornaIncompleto() {
        Tournament t = buildTournament(TournamentStatus.PREPARATION, List.of(
                new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED),
                new TeamRegistration("e2", "Equipo 2", RegistrationStatus.APPROVED)
        ));
        PreparationResult result = t.checkPreparation();
        assertFalse(result.isReadyToActivate());
        assertTrue(result.getMissingRequirements().stream().anyMatch(m -> m.contains("faltan")));
    }

    @Test
    void preparation_tresEquiposAprobadosYFechasValidas_retornaCompleto() {
        Tournament t = buildTournament(TournamentStatus.PREPARATION, List.of(
                new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED),
                new TeamRegistration("e2", "Equipo 2", RegistrationStatus.APPROVED),
                new TeamRegistration("e3", "Equipo 3", RegistrationStatus.APPROVED)
        ));
        PreparationResult result = t.checkPreparation();
        assertTrue(result.isReadyToActivate());
        assertTrue(result.getMissingRequirements().isEmpty());
    }

    @Test
    void preparation_sinFechas_retornaIncompleto() {
        Tournament t = new Tournament();
        t.setStatus(TournamentStatus.PREPARATION);
        t.setTeams(new java.util.ArrayList<>(List.of(
                new TeamRegistration("e1", "E1", RegistrationStatus.APPROVED),
                new TeamRegistration("e2", "E2", RegistrationStatus.APPROVED),
                new TeamRegistration("e3", "E3", RegistrationStatus.APPROVED)
        )));
        PreparationResult result = t.checkPreparation();
        assertFalse(result.isReadyToActivate());
        assertTrue(result.getMissingRequirements().stream().anyMatch(m -> m.contains("Fechas")));
    }

    // --- Eliminación de equipo ---

    @Test
    void removeTeam_torneoActivo_eliminaEquipoYMarcaPartidosPendientes() {
        Tournament t = buildTournament(TournamentStatus.ACTIVE, List.of(
                new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED),
                new TeamRegistration("e2", "Equipo 2", RegistrationStatus.APPROVED)
        ));
        Match pendingMatch = new Match("m1", "e1", "e2", MatchStatus.PENDING);
        t.setMatches(new java.util.ArrayList<>(List.of(pendingMatch)));

        List<Match> affected = t.removeTeam("e1", RemovalReason.DIRECT_DISQUALIFICATION);

        assertEquals(1, t.getTeams().size());
        assertEquals(1, affected.size());
        assertEquals(MatchStatus.FINISHED_NO_SHOW, affected.get(0).getStatus());
    }

    @Test
    void removeTeam_torneoEnPreparacion_lanza409() {
        Tournament t = buildTournament(TournamentStatus.PREPARATION, List.of(
                new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED)
        ));
        assertThrows(TeamRemovalNotAllowedException.class,
                () -> t.removeTeam("e1", RemovalReason.DISCIPLINARY_POINTS));
    }

    @Test
    void removeTeam_equipoNoInscrito_lanzaExcepcion() {
        Tournament t = buildTournament(TournamentStatus.IN_PROGRESS, List.of(
                new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED)
        ));
        assertThrows(TeamRemovalNotAllowedException.class,
                () -> t.removeTeam("e99", RemovalReason.DIRECT_DISQUALIFICATION));
    }
}
