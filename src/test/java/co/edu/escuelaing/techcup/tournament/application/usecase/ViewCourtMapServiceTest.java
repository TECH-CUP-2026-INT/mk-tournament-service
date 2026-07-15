package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewCourtMapUseCase.CourtMapEntry;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.CourtRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.ScheduledMatchRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ViewCourtMapServiceTest {

    private Tournament sampleTournament(List<Match> matches) {
        return Tournament.reconstruct(
                "t1", "TechCup", 4, BigDecimal.ZERO,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(10), LocalDate.now(),
                TournamentStatus.IN_PROGRESS, new ArrayList<>(), new ArrayList<>(matches)
        );
    }

    @Test
    void getCourtMap_whenCourtHasNoMatch_returnsAvailableEntry() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);

        Tournament tournament = sampleTournament(List.of());
        Court court = Court.reconstruct("c1", "t1", CourtSection.CANCHA_1, "Main court", null, null);

        when(tournamentRepository.findById("t1")).thenReturn(Optional.of(tournament));
        when(courtRepository.findAllByTournamentId("t1")).thenReturn(List.of(court));

        ViewCourtMapService service = new ViewCourtMapService(tournamentRepository, courtRepository, scheduledMatchRepository);
        List<CourtMapEntry> result = service.getCourtMap("t1");

        assertEquals(1, result.size());
        assertNull(result.get(0).match());
        assertNull(result.get(0).scheduledMatch());
        assertEquals("c1", result.get(0).court().getId());
    }

    @Test
    void getCourtMap_whenCourtHasScheduledMatch_returnsMatchAndSchedule() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);

        Match match = new Match("m1", "home", "away", MatchStatus.PENDING);
        Tournament tournament = sampleTournament(List.of(match));
        Court court = Court.reconstruct("c1", "t1", CourtSection.CANCHA_1, "Main court", null, "m1");
        ScheduledMatch scheduledMatch = ScheduledMatch.reconstruct(
                "sm1", "m1", "c1", "ref-1", LocalDate.of(2026, 8, 5), LocalTime.of(9, 0));

        when(tournamentRepository.findById("t1")).thenReturn(Optional.of(tournament));
        when(courtRepository.findAllByTournamentId("t1")).thenReturn(List.of(court));
        when(scheduledMatchRepository.findByMatchupId("m1")).thenReturn(Optional.of(scheduledMatch));

        ViewCourtMapService service = new ViewCourtMapService(tournamentRepository, courtRepository, scheduledMatchRepository);
        List<CourtMapEntry> result = service.getCourtMap("t1");

        assertEquals(1, result.size());
        assertEquals("m1", result.get(0).match().getMatchId());
        assertEquals(LocalDate.of(2026, 8, 5), result.get(0).scheduledMatch().getMatchDate());
    }

    @Test
    void getCourtMap_whenTournamentNotFound_throwsException() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);

        when(tournamentRepository.findById("missing")).thenReturn(Optional.empty());

        ViewCourtMapService service = new ViewCourtMapService(tournamentRepository, courtRepository, scheduledMatchRepository);

        assertThrows(TournamentNotFoundException.class, () -> service.getCourtMap("missing"));
    }
}
