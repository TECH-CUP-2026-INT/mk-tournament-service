package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidRulebookFileException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.AttachRulebookUseCase.AttachRulebookCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.RulebookStoragePort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AttachRulebookServiceTest {

    private Tournament sampleTournament() {
        return Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                TournamentStatus.DRAFT
        );
    }

    @Test
    void attach_pdfValidoYTorneoExiste_guardaYRetornaTorneoConFileId() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RulebookStoragePort storageMock = mock(RulebookStoragePort.class);
        Tournament tournament = sampleTournament();

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));
        when(storageMock.store(anyString(), anyString(), anyLong(), any())).thenReturn("file-123");
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        AttachRulebookService service = new AttachRulebookService(repositoryMock, storageMock);

        AttachRulebookCommand command = new AttachRulebookCommand(
                "1", "reglamento.pdf", "application/pdf", 1024L,
                new ByteArrayInputStream("contenido".getBytes())
        );

        Tournament result = service.attach(command);

        assertEquals("file-123", result.getRulebookFileId());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void attach_torneoNoExiste_lanzaTournamentNotFoundException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RulebookStoragePort storageMock = mock(RulebookStoragePort.class);

        when(repositoryMock.findById("99")).thenReturn(Optional.empty());

        AttachRulebookService service = new AttachRulebookService(repositoryMock, storageMock);

        AttachRulebookCommand command = new AttachRulebookCommand(
                "99", "reglamento.pdf", "application/pdf", 1024L,
                new ByteArrayInputStream("contenido".getBytes())
        );

        assertThrows(TournamentNotFoundException.class, () -> service.attach(command));
    }

    @Test
    void attach_archivoNoEsPdf_lanzaInvalidRulebookFileException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RulebookStoragePort storageMock = mock(RulebookStoragePort.class);
        Tournament tournament = sampleTournament();

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));

        AttachRulebookService service = new AttachRulebookService(repositoryMock, storageMock);

        AttachRulebookCommand command = new AttachRulebookCommand(
                "1", "reglamento.png", "image/png", 1024L,
                new ByteArrayInputStream("contenido".getBytes())
        );

        assertThrows(InvalidRulebookFileException.class, () -> service.attach(command));
    }

    @Test
    void attach_archivoSuperaLimite_lanzaInvalidRulebookFileException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        RulebookStoragePort storageMock = mock(RulebookStoragePort.class);
        Tournament tournament = sampleTournament();

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));

        AttachRulebookService service = new AttachRulebookService(repositoryMock, storageMock);

        AttachRulebookCommand command = new AttachRulebookCommand(
                "1", "reglamento.pdf", "application/pdf", InvalidRulebookFileException.MAX_SIZE_BYTES + 1,
                new ByteArrayInputStream("contenido".getBytes())
        );

        assertThrows(InvalidRulebookFileException.class, () -> service.attach(command));
    }
}
