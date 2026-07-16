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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InactivateMatchServiceTest {

    private Tournament tournamentWithMatch(Match match) {
        return Tournament.builder()
                .id(UUID.randomUUID()).name("TechCup").numberOfTeams(4).cost(BigDecimal.ZERO)
                .startDate(LocalDate.now().plusDays(2)).endDate(LocalDate.now().plusDays(10))
                .registrationDeadline(LocalDate.now())
                .status(TournamentStatus.IN_PROGRESS).teams(new ArrayList<>())
                .matches(new ArrayList<>(List.of(match)))
                .reconstruct();
    }

    @Test
    void execute_inactivate_inactivaYGuardaElTorneo() {
        UUID matchId = UUID.randomUUID();
        Match match = new Match(matchId, UUID.randomUUID(), UUID.randomUUID(), MatchStatus.PENDING);
        Tournament tournament = tournamentWithMatch(match);
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findByMatchId(matchId)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        InactivateMatchService service = new InactivateMatchService(repositoryMock);

        Match result = service.execute(new InactivateMatchCommand(matchId, MatchActivationAction.INACTIVATE));

        assertFalse(result.isActive());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void execute_reactivate_reactivaYGuardaElTorneo() {
        UUID matchId = UUID.randomUUID();
        Match match = new Match(matchId, UUID.randomUUID(), UUID.randomUUID(), MatchStatus.PENDING);
        match.inactivate();
        Tournament tournament = tournamentWithMatch(match);
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findByMatchId(matchId)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        InactivateMatchService service = new InactivateMatchService(repositoryMock);

        Match result = service.execute(new InactivateMatchCommand(matchId, MatchActivationAction.REACTIVATE));

        assertTrue(result.isActive());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void execute_matchNoExiste_lanzaMatchupNotFoundException() {
        UUID matchId = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findByMatchId(matchId)).thenReturn(Optional.empty());

        InactivateMatchService service = new InactivateMatchService(repositoryMock);

        assertThrows(MatchupNotFoundException.class,
                () -> service.execute(new InactivateMatchCommand(matchId, MatchActivationAction.INACTIVATE)));
    }
}
