package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchActivationAction;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateMatchUseCase.InactivateMatchCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InactivateMatchServiceTest {

    private Tournament tournamentWithMatch(Match match) {
        return Tournament.reconstruct(
                "t1", "TechCup", 4, BigDecimal.ZERO,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10),
                LocalDate.now(),
                TournamentStatus.IN_PROGRESS,
                new ArrayList<>(),
                new ArrayList<>(List.of(match))
        );
    }

    @Test
    void execute_inactivate_inactivaYGuardaElTorneo() {
        Match match = new Match("m1", "home", "away", MatchStatus.PENDING);
        Tournament tournament = tournamentWithMatch(match);
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findByMatchId("m1")).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        InactivateMatchService service = new InactivateMatchService(repositoryMock);

        Match result = service.execute(new InactivateMatchCommand("m1", MatchActivationAction.INACTIVATE));

        assertFalse(result.isActive());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void execute_reactivate_reactivaYGuardaElTorneo() {
        Match match = new Match("m1", "home", "away", MatchStatus.PENDING);
        match.inactivate();
        Tournament tournament = tournamentWithMatch(match);
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findByMatchId("m1")).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        InactivateMatchService service = new InactivateMatchService(repositoryMock);

        Match result = service.execute(new InactivateMatchCommand("m1", MatchActivationAction.REACTIVATE));

        assertTrue(result.isActive());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void execute_matchNoExiste_lanzaMatchupNotFoundException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findByMatchId("m99")).thenReturn(Optional.empty());

        InactivateMatchService service = new InactivateMatchService(repositoryMock);

        assertThrows(MatchupNotFoundException.class,
                () -> service.execute(new InactivateMatchCommand("m99", MatchActivationAction.INACTIVATE)));
    }
}
