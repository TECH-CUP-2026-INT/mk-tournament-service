package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.DisqualificationReason;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;

import co.edu.escuelaing.techcup.tournament.domain.exception.TeamDisqualificationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentInactiveException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentDisqualificationTest {

    private Tournament sampleTournament(List<TeamRegistration> teams) {
        return Tournament.reconstruct(
                UUID.randomUUID(), "Copa Enero", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                null, null, TournamentStatus.ACTIVE,
                teams, List.of(), null, null
        );
    }

    @Test
    void disqualifyTeam_equipoInscrito_quedaDescalificadoYSePreserva() {
        UUID teamId = UUID.randomUUID();
        Tournament tournament = sampleTournament(List.of(
                new TeamRegistration(teamId, "Equipo 1", RegistrationStatus.APPROVED)
        ));

        tournament.disqualifyTeam(teamId, DisqualificationReason.RULES_VIOLATION);

        assertEquals(1, tournament.getTeams().size());
        assertEquals(RegistrationStatus.DISQUALIFIED, tournament.getTeams().get(0).getRegistrationStatus());
    }

    @Test
    void disqualifyTeam_equipoNoInscrito_lanzaExcepcion() {
        UUID teamId = UUID.randomUUID();
        UUID otherTeamId = UUID.randomUUID();
        Tournament tournament = sampleTournament(List.of(
                new TeamRegistration(teamId, "Equipo 1", RegistrationStatus.APPROVED)
        ));

        assertThrows(TeamDisqualificationNotAllowedException.class,
                () -> tournament.disqualifyTeam(otherTeamId, DisqualificationReason.RULES_VIOLATION));
    }

    @Test
    void disqualifyTeam_equipoYaDescalificado_lanzaExcepcion() {
        UUID teamId = UUID.randomUUID();
        Tournament tournament = sampleTournament(List.of(
                new TeamRegistration(teamId, "Equipo 1", RegistrationStatus.APPROVED)
        ));
        tournament.disqualifyTeam(teamId, DisqualificationReason.POINTS_STANDING);

        assertThrows(TeamDisqualificationNotAllowedException.class,
                () -> tournament.disqualifyTeam(teamId, DisqualificationReason.POINTS_STANDING));
    }

    @Test
    void disqualifyTeam_sinMotivo_lanzaExcepcion() {
        UUID teamId = UUID.randomUUID();
        Tournament tournament = sampleTournament(List.of(
                new TeamRegistration(teamId, "Equipo 1", RegistrationStatus.APPROVED)
        ));

        assertThrows(TeamDisqualificationNotAllowedException.class,
                () -> tournament.disqualifyTeam(teamId, null));
    }

    @Test
    void disqualifyTeam_torneoInactivo_lanzaExcepcion() {
        UUID teamId = UUID.randomUUID();
        Tournament tournament = sampleTournament(List.of(
                new TeamRegistration(teamId, "Equipo 1", RegistrationStatus.APPROVED)
        ));
        tournament.inactivate();

        assertThrows(TournamentInactiveException.class,
                () -> tournament.disqualifyTeam(teamId, DisqualificationReason.RULES_VIOLATION));
    }
}
