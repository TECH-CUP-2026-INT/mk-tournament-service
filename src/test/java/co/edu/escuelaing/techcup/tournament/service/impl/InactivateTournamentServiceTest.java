package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentInactivationAction;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateTournamentUseCase.InactivateTournamentCommand;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InactivateTournamentServiceTest {

    private Tournament sampleTournament(TournamentStatus status) {
        return Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                status
        );
    }

    @Test
    void execute_inactivate_inactivaYGuardaElTorneo() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        InactivateTournamentService service = new InactivateTournamentService(repositoryMock);

        Tournament result = service.execute(new InactivateTournamentCommand("1", TournamentInactivationAction.INACTIVATE));

        assertFalse(result.isActive());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void execute_reactivate_reactivaYGuardaElTorneo() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);
        tournament.inactivate();

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        InactivateTournamentService service = new InactivateTournamentService(repositoryMock);

        Tournament result = service.execute(new InactivateTournamentCommand("1", TournamentInactivationAction.REACTIVATE));

        assertTrue(result.isActive());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void execute_torneoNoExiste_lanzaTournamentNotFoundException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findById("99")).thenReturn(Optional.empty());

        InactivateTournamentService service = new InactivateTournamentService(repositoryMock);

        assertThrows(TournamentNotFoundException.class,
                () -> service.execute(new InactivateTournamentCommand("99", TournamentInactivationAction.INACTIVATE)));
    }

    @Test
    void execute_torneoFinalizado_lanzaTournamentInactivationNotAllowedException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(TournamentStatus.FINISHED);
        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));

        InactivateTournamentService service = new InactivateTournamentService(repositoryMock);

        assertThrows(TournamentInactivationNotAllowedException.class,
                () -> service.execute(new InactivateTournamentCommand("1", TournamentInactivationAction.INACTIVATE)));
    }
}
