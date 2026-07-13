package co.edu.escuelaing.techcup.tournament.service;

import co.edu.escuelaing.techcup.tournament.exception.TeamDisqualificationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentInactiveException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentDisqualificationTest {

    private Tournament sampleTournament(List<TeamRegistration> teams) {
        return Tournament.reconstruct(
                "1", "Copa Enero", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                null, null, TournamentStatus.ACTIVE,
                teams, List.of(), null, null
        );
    }

    @Test
    void disqualifyTeam_equipoInscrito_quedaDescalificadoYSePreserva() {
        Tournament tournament = sampleTournament(List.of(
                new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED)
        ));

        tournament.disqualifyTeam("e1", DisqualificationReason.RULES_VIOLATION);

        assertEquals(1, tournament.getTeams().size());
        assertEquals(RegistrationStatus.DISQUALIFIED, tournament.getTeams().get(0).getRegistrationStatus());
    }

    @Test
    void disqualifyTeam_equipoNoInscrito_lanzaExcepcion() {
        Tournament tournament = sampleTournament(List.of(
                new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED)
        ));

        assertThrows(TeamDisqualificationNotAllowedException.class,
                () -> tournament.disqualifyTeam("e2", DisqualificationReason.RULES_VIOLATION));
    }

    @Test
    void disqualifyTeam_equipoYaDescalificado_lanzaExcepcion() {
        Tournament tournament = sampleTournament(List.of(
                new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED)
        ));
        tournament.disqualifyTeam("e1", DisqualificationReason.POINTS_STANDING);

        assertThrows(TeamDisqualificationNotAllowedException.class,
                () -> tournament.disqualifyTeam("e1", DisqualificationReason.POINTS_STANDING));
    }

    @Test
    void disqualifyTeam_sinMotivo_lanzaExcepcion() {
        Tournament tournament = sampleTournament(List.of(
                new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED)
        ));

        assertThrows(TeamDisqualificationNotAllowedException.class,
                () -> tournament.disqualifyTeam("e1", null));
    }

    @Test
    void disqualifyTeam_torneoInactivo_lanzaExcepcion() {
        Tournament tournament = sampleTournament(List.of(
                new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED)
        ));
        tournament.inactivate();

        assertThrows(TournamentInactiveException.class,
                () -> tournament.disqualifyTeam("e1", DisqualificationReason.RULES_VIOLATION));
    }
}
