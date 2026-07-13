package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentCannotBeFinalizedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.RecognitionAwardPort;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FinalizeTournamentServiceTest {

    private Tournament inProgressTournament() {
        return Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), LocalDate.of(2026, 2, 20),
                TournamentStatus.IN_PROGRESS
        );
    }

    @Test
    void finalizeTournament_whenInProgressAndEndDateReached_setsStatusFinishedYDisparaPremios() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RecognitionAwardPort awardPortMock = mock(RecognitionAwardPort.class);
        Tournament tournament = inProgressTournament();

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        FinalizeTournamentService service = new FinalizeTournamentService(repositoryMock, awardPortMock);
        Tournament result = service.finalizeTournament("1");

        assertEquals(TournamentStatus.FINISHED, result.getStatus());
        verify(repositoryMock).save(tournament);
        verify(awardPortMock).triggerAwards("1");
    }

    @Test
    void finalizeTournament_cuandoFallaElDisparoDePremios_igualFinalizaSinLanzarExcepcion() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RecognitionAwardPort awardPortMock = mock(RecognitionAwardPort.class);
        Tournament tournament = inProgressTournament();

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);
        doThrow(new RuntimeException("proveedor de premios caído")).when(awardPortMock).triggerAwards("1");

        FinalizeTournamentService service = new FinalizeTournamentService(repositoryMock, awardPortMock);

        Tournament result = assertDoesNotThrow(() -> service.finalizeTournament("1"));

        assertEquals(TournamentStatus.FINISHED, result.getStatus());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void finalizeTournament_whenTournamentNotFound_throwsException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RecognitionAwardPort awardPortMock = mock(RecognitionAwardPort.class);
        when(repositoryMock.findById("99")).thenReturn(Optional.empty());

        FinalizeTournamentService service = new FinalizeTournamentService(repositoryMock, awardPortMock);

        assertThrows(TournamentNotFoundException.class, () -> service.finalizeTournament("99"));
    }

    @Test
    void finalizeTournament_whenEndDateNotReached_throwsException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RecognitionAwardPort awardPortMock = mock(RecognitionAwardPort.class);

        Tournament tournament = Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.now().plusDays(10), LocalDate.of(2026, 2, 20),
                TournamentStatus.IN_PROGRESS
        );

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));

        FinalizeTournamentService service = new FinalizeTournamentService(repositoryMock, awardPortMock);

        assertThrows(TournamentCannotBeFinalizedException.class,
                () -> service.finalizeTournament("1"));
    }

    @Test
    void finalizeTournament_whenAlreadyFinished_throwsException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RecognitionAwardPort awardPortMock = mock(RecognitionAwardPort.class);

        Tournament tournament = Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), LocalDate.of(2026, 2, 20),
                TournamentStatus.FINISHED
        );

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));

        FinalizeTournamentService service = new FinalizeTournamentService(repositoryMock, awardPortMock);

        assertThrows(TournamentCannotBeFinalizedException.class,
                () -> service.finalizeTournament("1"));
    }
}
