package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RecordPenaltyShootoutWinnerUseCase.RecordPenaltyShootoutWinnerCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecordPenaltyShootoutWinnerServiceTest {

    @Test
    void recordWinner_whenTiedFinalMatch_recordsWinnerAndPersists() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Match finalMatch = new Match("final-1", "home", "away", MatchStatus.FINISHED,
                true, 1, 1, null);
        Tournament tournament = Tournament.reconstruct(
                "t1", "TechCup", 4, BigDecimal.ZERO,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(10), LocalDate.now(),
                TournamentStatus.IN_PROGRESS, new ArrayList<>(), new ArrayList<>(List.of(finalMatch))
        );

        when(repository.findById("t1")).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        RecordPenaltyShootoutWinnerService service = new RecordPenaltyShootoutWinnerService(repository);
        service.recordWinner(new RecordPenaltyShootoutWinnerCommand("t1", "final-1", "away"));

        assertEquals("away", tournament.getMatches().get(0).getPenaltyShootoutWinnerTeamId());
        verify(repository).save(tournament);
    }

    @Test
    void recordWinner_whenNotTied_throwsException() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Match finalMatch = new Match("final-1", "home", "away", MatchStatus.FINISHED,
                true, 2, 1, null);
        Tournament tournament = Tournament.reconstruct(
                "t1", "TechCup", 4, BigDecimal.ZERO,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(10), LocalDate.now(),
                TournamentStatus.IN_PROGRESS, new ArrayList<>(), new ArrayList<>(List.of(finalMatch))
        );

        when(repository.findById("t1")).thenReturn(Optional.of(tournament));

        RecordPenaltyShootoutWinnerService service = new RecordPenaltyShootoutWinnerService(repository);

        assertThrows(ChampionAssignmentNotAllowedException.class,
                () -> service.recordWinner(new RecordPenaltyShootoutWinnerCommand("t1", "final-1", "away")));
    }

    @Test
    void recordWinner_whenTournamentNotFound_throwsException() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findById("missing")).thenReturn(Optional.empty());

        RecordPenaltyShootoutWinnerService service = new RecordPenaltyShootoutWinnerService(repository);

        assertThrows(TournamentNotFoundException.class,
                () -> service.recordWinner(new RecordPenaltyShootoutWinnerCommand("missing", "final-1", "away")));
    }
}
