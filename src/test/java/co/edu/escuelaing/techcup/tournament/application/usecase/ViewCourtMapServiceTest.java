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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ViewCourtMapServiceTest {

    private final UUID tournamentId = UUID.randomUUID();

    private Tournament sampleTournament(List<Match> matches) {
        return Tournament.reconstruct(
                tournamentId, "TechCup", 4, BigDecimal.ZERO,
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
        UUID courtId = UUID.randomUUID();
        Court court = Court.reconstruct(courtId, tournamentId, CourtSection.CANCHA_1, "Main court", null, null);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(courtRepository.findAllByTournamentId(tournamentId)).thenReturn(List.of(court));

        ViewCourtMapService service = new ViewCourtMapService(tournamentRepository, courtRepository, scheduledMatchRepository);
        List<CourtMapEntry> result = service.getCourtMap(tournamentId);

        assertEquals(1, result.size());
        assertNull(result.get(0).match());
        assertNull(result.get(0).scheduledMatch());
        assertEquals(courtId, result.get(0).court().getId());
    }

    @Test
    void getCourtMap_whenCourtHasScheduledMatch_returnsMatchAndSchedule() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);

        UUID matchId = UUID.randomUUID();
        UUID courtId = UUID.randomUUID();
        Match match = new Match(matchId, UUID.randomUUID(), UUID.randomUUID(), MatchStatus.PENDING);
        Tournament tournament = sampleTournament(List.of(match));
        Court court = Court.reconstruct(courtId, tournamentId, CourtSection.CANCHA_1, "Main court", null, matchId);
        ScheduledMatch scheduledMatch = ScheduledMatch.reconstruct(
                UUID.randomUUID(), matchId, courtId, UUID.randomUUID(), LocalDate.of(2026, 8, 5), LocalTime.of(9, 0));

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(courtRepository.findAllByTournamentId(tournamentId)).thenReturn(List.of(court));
        when(scheduledMatchRepository.findByMatchupId(matchId)).thenReturn(Optional.of(scheduledMatch));

        ViewCourtMapService service = new ViewCourtMapService(tournamentRepository, courtRepository, scheduledMatchRepository);
        List<CourtMapEntry> result = service.getCourtMap(tournamentId);

        assertEquals(1, result.size());
        assertEquals(matchId, result.get(0).match().getMatchId());
        assertEquals(LocalDate.of(2026, 8, 5), result.get(0).scheduledMatch().getMatchDate());
    }

    @Test
    void getCourtMap_whenTournamentNotFound_throwsException() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);

        UUID missing = UUID.randomUUID();
        when(tournamentRepository.findById(missing)).thenReturn(Optional.empty());

        ViewCourtMapService service = new ViewCourtMapService(tournamentRepository, courtRepository, scheduledMatchRepository);

        assertThrows(TournamentNotFoundException.class, () -> service.getCourtMap(missing));
    }
}
