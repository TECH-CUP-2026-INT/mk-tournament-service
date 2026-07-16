package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewMatchupsServiceTest {

    private TournamentRepositoryPort tournamentRepository;
    private ViewMatchupsService service;

    @BeforeEach
    void setUp() {
        tournamentRepository = mock(TournamentRepositoryPort.class);
        service = new ViewMatchupsService(tournamentRepository);
    }

    @Test
    void getMatchups_whenMatchesExist_returnsMatchList() {
        UUID tournamentId = UUID.randomUUID();
        UUID m1 = UUID.randomUUID();
        UUID m2 = UUID.randomUUID();
        Tournament tournament = new Tournament(tournamentId, "Copa ECI", TournamentStatus.IN_PROGRESS);
        tournament.setMatches(List.of(
                new Match(m1, UUID.randomUUID(), UUID.randomUUID(), MatchStatus.PENDING),
                new Match(m2, UUID.randomUUID(), UUID.randomUUID(), MatchStatus.FINISHED)
        ));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        List<Match> result = service.getMatchups(tournamentId);

        assertEquals(2, result.size());
        assertEquals(m1, result.get(0).getMatchId());
        assertEquals(m2, result.get(1).getMatchId());
    }

    @Test
    void getMatchups_whenNoMatchesGenerated_returnsEmptyList() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament(tournamentId, "Copa ECI", TournamentStatus.ACTIVE);
        tournament.setMatches(List.of());
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        List<Match> result = service.getMatchups(tournamentId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getMatchups_whenTournamentNotFound_throwsTournamentNotFoundException() {
        UUID unknown = UUID.randomUUID();
        when(tournamentRepository.findById(unknown)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> service.getMatchups(unknown));
    }

    @Test
    void getMatchups_pendingSlotHasNullTeamIds() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament(tournamentId, "Copa ECI", TournamentStatus.IN_PROGRESS);
        tournament.setMatches(List.of(
                new Match(UUID.randomUUID(), null, null, MatchStatus.PENDING)
        ));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        List<Match> result = service.getMatchups(tournamentId);

        assertNull(result.get(0).getHomeTeamId());
        assertNull(result.get(0).getAwayTeamId());
    }
}
