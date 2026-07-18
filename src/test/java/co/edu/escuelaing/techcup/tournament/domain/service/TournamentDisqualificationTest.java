package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.DisqualificationReason;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
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
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentDisqualificationTest {

    private Tournament sampleTournament(List<TeamRegistration> teams) {
        return sampleTournament(teams, List.of());
    }

    private Tournament sampleTournament(List<TeamRegistration> teams, List<Match> matches) {
        return Tournament.builder()
                .id(UUID.randomUUID()).name("Copa Enero").type(TournamentType.NORMAL).format(TournamentFormat.BRACKETS)
                .numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(TournamentStatus.ACTIVE).teams(teams).matches(new ArrayList<>(matches))
                .reconstruct();
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
    void disqualifyTeam_conPartidosPendientes_losMarcaComoWalkoverYDejaLosDemasIntactos() {
        UUID teamId = UUID.randomUUID();
        UUID rivalId = UUID.randomUUID();
        UUID otherTeamId = UUID.randomUUID();
        Match pendingMatch = new Match(UUID.randomUUID(), teamId, rivalId, MatchStatus.PENDING);
        Match unrelatedFinishedMatch = Match.builder().matchId(UUID.randomUUID())
                .homeTeamId(rivalId).awayTeamId(otherTeamId).status(MatchStatus.FINISHED)
                .homeScore(1).awayScore(0).build();
        Tournament tournament = sampleTournament(
                List.of(new TeamRegistration(teamId, "Equipo 1", RegistrationStatus.APPROVED)),
                List.of(pendingMatch, unrelatedFinishedMatch));

        tournament.disqualifyTeam(teamId, DisqualificationReason.RULES_VIOLATION);

        assertEquals(MatchStatus.FINISHED_NO_SHOW, pendingMatch.getStatus());
        assertEquals(MatchStatus.FINISHED, unrelatedFinishedMatch.getStatus());
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
