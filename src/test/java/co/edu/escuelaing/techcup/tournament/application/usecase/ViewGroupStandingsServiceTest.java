package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentInactiveException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.DisqualificationReason;
import co.edu.escuelaing.techcup.tournament.domain.model.GroupTable;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ViewGroupStandingsServiceTest {

    private Tournament sampleTournament(List<TeamRegistration> teams, List<Match> matches) {
        return Tournament.builder()
                .id(UUID.randomUUID()).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(TournamentStatus.IN_PROGRESS).teams(teams).matches(new ArrayList<>(matches))
                .reconstruct();
    }

    @Test
    void getStandings_torneoNoExiste_lanzaTournamentNotFoundException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findById(id)).thenReturn(Optional.empty());

        ViewGroupStandingsService service = new ViewGroupStandingsService(repository);

        assertThrows(TournamentNotFoundException.class, () -> service.getStandings(id));
    }

    @Test
    void getStandings_torneoInactivo_lanzaTournamentInactiveException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(List.of(), List.of());
        tournament.inactivate();
        when(repository.findById(id)).thenReturn(Optional.of(tournament));

        ViewGroupStandingsService service = new ViewGroupStandingsService(repository);

        assertThrows(TournamentInactiveException.class, () -> service.getStandings(id));
    }

    @Test
    void getStandings_equipoDescalificado_noAcreditaSuWalkoverComoVictoria() {
        UUID id = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        UUID rivalId = UUID.randomUUID();
        TeamRegistration disqualifiedTeam = new TeamRegistration(teamId, "Equipo 1", RegistrationStatus.APPROVED);
        Match groupMatch = Match.builder().matchId(UUID.randomUUID()).homeTeamId(teamId).awayTeamId(rivalId)
                .status(MatchStatus.PENDING).groupName("Grupo A").build();
        Tournament tournament = sampleTournament(
                List.of(disqualifiedTeam, new TeamRegistration(rivalId, "Equipo 2", RegistrationStatus.APPROVED)),
                List.of(groupMatch));

        tournament.disqualifyTeam(teamId, DisqualificationReason.RULES_VIOLATION);

        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findById(id)).thenReturn(Optional.of(tournament));

        ViewGroupStandingsService service = new ViewGroupStandingsService(repository);
        List<GroupTable> tables = service.getStandings(id);

        var rivalStanding = tables.get(0).standings().stream()
                .filter(s -> s.teamId().equals(rivalId)).findFirst().orElseThrow();
        var disqualifiedStanding = tables.get(0).standings().stream()
                .filter(s -> s.teamId().equals(teamId)).findFirst().orElseThrow();

        assertEquals(3, rivalStanding.points());
        assertEquals(0, disqualifiedStanding.points());
    }
}
