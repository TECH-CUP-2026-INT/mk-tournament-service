package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentPauseNotAllowedException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentPauseAction;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.PauseTournamentUseCase.PauseTournamentCommand;
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

class PauseTournamentServiceTest {

    private Tournament sampleTournament(TournamentStatus status) {
        return Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                status
        );
    }

    @Test
    void execute_pause_pausaYGuardaElTorneo() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        PauseTournamentService service = new PauseTournamentService(repositoryMock);

        Tournament result = service.execute(new PauseTournamentCommand("1", TournamentPauseAction.PAUSE));

        assertTrue(result.isPaused());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void execute_resume_reanudaYGuardaElTorneo() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);
        tournament.pause();

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        PauseTournamentService service = new PauseTournamentService(repositoryMock);

        Tournament result = service.execute(new PauseTournamentCommand("1", TournamentPauseAction.RESUME));

        assertFalse(result.isPaused());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void execute_torneoNoExiste_lanzaTournamentNotFoundException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findById("99")).thenReturn(Optional.empty());

        PauseTournamentService service = new PauseTournamentService(repositoryMock);

        assertThrows(TournamentNotFoundException.class,
                () -> service.execute(new PauseTournamentCommand("99", TournamentPauseAction.PAUSE)));
    }

    @Test
    void execute_torneoFinalizado_lanzaTournamentPauseNotAllowedException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(TournamentStatus.FINISHED);
        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));

        PauseTournamentService service = new PauseTournamentService(repositoryMock);

        assertThrows(TournamentPauseNotAllowedException.class,
                () -> service.execute(new PauseTournamentCommand("1", TournamentPauseAction.PAUSE)));
    }
}
