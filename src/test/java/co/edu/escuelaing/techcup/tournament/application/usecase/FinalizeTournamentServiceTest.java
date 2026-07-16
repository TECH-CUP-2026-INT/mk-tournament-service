package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentCannotBeFinalizedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.RecognitionAwardPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentEventPublisherPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FinalizeTournamentServiceTest {

    private Tournament inProgressTournament(UUID id) {
        return Tournament.reconstruct(
                id, "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), LocalDate.of(2026, 2, 20),
                TournamentStatus.IN_PROGRESS
        );
    }

    @Test
    void finalizeTournament_whenInProgressAndEndDateReached_setsStatusFinishedYDisparaPremios() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RecognitionAwardPort awardPortMock = mock(RecognitionAwardPort.class);
        TournamentEventPublisherPort eventPublisherMock = mock(TournamentEventPublisherPort.class);
        Tournament tournament = inProgressTournament(id);

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        FinalizeTournamentService service = new FinalizeTournamentService(repositoryMock, awardPortMock, eventPublisherMock);
        Tournament result = service.finalizeTournament(id);

        assertEquals(TournamentStatus.FINISHED, result.getStatus());
        verify(repositoryMock).save(tournament);
        verify(awardPortMock).triggerAwards(id);
        verify(eventPublisherMock).publishTournamentFinalized(id);
    }

    @Test
    void finalizeTournament_cuandoFallaElDisparoDePremios_igualFinalizaSinLanzarExcepcion() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RecognitionAwardPort awardPortMock = mock(RecognitionAwardPort.class);
        TournamentEventPublisherPort eventPublisherMock = mock(TournamentEventPublisherPort.class);
        Tournament tournament = inProgressTournament(id);

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);
        doThrow(new RuntimeException("proveedor de premios caído")).when(awardPortMock).triggerAwards(id);

        FinalizeTournamentService service = new FinalizeTournamentService(repositoryMock, awardPortMock, eventPublisherMock);

        Tournament result = assertDoesNotThrow(() -> service.finalizeTournament(id));

        assertEquals(TournamentStatus.FINISHED, result.getStatus());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void finalizeTournament_whenTournamentNotFound_throwsException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RecognitionAwardPort awardPortMock = mock(RecognitionAwardPort.class);
        TournamentEventPublisherPort eventPublisherMock = mock(TournamentEventPublisherPort.class);
        when(repositoryMock.findById(id)).thenReturn(Optional.empty());

        FinalizeTournamentService service = new FinalizeTournamentService(repositoryMock, awardPortMock, eventPublisherMock);

        assertThrows(TournamentNotFoundException.class, () -> service.finalizeTournament(id));
    }

    @Test
    void finalizeTournament_whenEndDateNotReached_throwsException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RecognitionAwardPort awardPortMock = mock(RecognitionAwardPort.class);
        TournamentEventPublisherPort eventPublisherMock = mock(TournamentEventPublisherPort.class);

        Tournament tournament = Tournament.reconstruct(
                id, "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.now().plusDays(10), LocalDate.of(2026, 2, 20),
                TournamentStatus.IN_PROGRESS
        );

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));

        FinalizeTournamentService service = new FinalizeTournamentService(repositoryMock, awardPortMock, eventPublisherMock);

        assertThrows(TournamentCannotBeFinalizedException.class,
                () -> service.finalizeTournament(id));
    }

    @Test
    void finalizeTournament_whenAlreadyFinished_throwsException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RecognitionAwardPort awardPortMock = mock(RecognitionAwardPort.class);
        TournamentEventPublisherPort eventPublisherMock = mock(TournamentEventPublisherPort.class);

        Tournament tournament = Tournament.reconstruct(
                id, "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), LocalDate.of(2026, 2, 20),
                TournamentStatus.FINISHED
        );

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));

        FinalizeTournamentService service = new FinalizeTournamentService(repositoryMock, awardPortMock, eventPublisherMock);

        assertThrows(TournamentCannotBeFinalizedException.class,
                () -> service.finalizeTournament(id));
    }
}
