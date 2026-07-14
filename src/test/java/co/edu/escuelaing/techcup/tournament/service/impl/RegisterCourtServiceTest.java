package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.InvalidCourtImageException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.CourtSection;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.CourtImageStoragePort;
import co.edu.escuelaing.techcup.tournament.service.ports.CourtRepositoryPort;
import co.edu.escuelaing.techcup.tournament.service.ports.RegisterCourtUseCase.RegisterCourtCommand;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterCourtServiceTest {

    private Tournament sampleTournament() {
        return Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                TournamentStatus.DRAFT
        );
    }

    @Test
    void register_sinImagen_guardaYRetornaCourt() {
        TournamentRepositoryPort tournamentRepositoryMock = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepositoryMock = mock(CourtRepositoryPort.class);
        CourtImageStoragePort imageStorageMock = mock(CourtImageStoragePort.class);

        when(tournamentRepositoryMock.findById("1")).thenReturn(Optional.of(sampleTournament()));
        when(courtRepositoryMock.save(any(Court.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterCourtService service = new RegisterCourtService(tournamentRepositoryMock, courtRepositoryMock, imageStorageMock);

        RegisterCourtCommand command = new RegisterCourtCommand(
                "1", CourtSection.CANCHA_1, "Cancha techada", null, null, null, null
        );

        Court result = service.register(command);

        assertEquals(CourtSection.CANCHA_1, result.getSection());
        assertNull(result.getImageId());
        verify(imageStorageMock, never()).store(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void register_conImagenValida_guardaImagenYCourt() {
        TournamentRepositoryPort tournamentRepositoryMock = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepositoryMock = mock(CourtRepositoryPort.class);
        CourtImageStoragePort imageStorageMock = mock(CourtImageStoragePort.class);

        when(tournamentRepositoryMock.findById("1")).thenReturn(Optional.of(sampleTournament()));
        when(imageStorageMock.store(anyString(), anyString(), anyLong(), any())).thenReturn("image-123");
        when(courtRepositoryMock.save(any(Court.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterCourtService service = new RegisterCourtService(tournamentRepositoryMock, courtRepositoryMock, imageStorageMock);

        RegisterCourtCommand command = new RegisterCourtCommand(
                "1", CourtSection.CANCHA_2, null,
                "cancha2.jpg", "image/jpeg", 2048L, new ByteArrayInputStream("contenido".getBytes())
        );

        Court result = service.register(command);

        assertEquals("image-123", result.getImageId());
    }

    @Test
    void register_torneoNoExiste_lanzaTournamentNotFoundException() {
        TournamentRepositoryPort tournamentRepositoryMock = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepositoryMock = mock(CourtRepositoryPort.class);
        CourtImageStoragePort imageStorageMock = mock(CourtImageStoragePort.class);

        when(tournamentRepositoryMock.findById("99")).thenReturn(Optional.empty());

        RegisterCourtService service = new RegisterCourtService(tournamentRepositoryMock, courtRepositoryMock, imageStorageMock);

        RegisterCourtCommand command = new RegisterCourtCommand(
                "99", CourtSection.CANCHA_1, null, null, null, null, null
        );

        assertThrows(TournamentNotFoundException.class, () -> service.register(command));
    }

    @Test
    void register_imagenNoEsJpgOPng_lanzaInvalidCourtImageException() {
        TournamentRepositoryPort tournamentRepositoryMock = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepositoryMock = mock(CourtRepositoryPort.class);
        CourtImageStoragePort imageStorageMock = mock(CourtImageStoragePort.class);

        when(tournamentRepositoryMock.findById("1")).thenReturn(Optional.of(sampleTournament()));

        RegisterCourtService service = new RegisterCourtService(tournamentRepositoryMock, courtRepositoryMock, imageStorageMock);

        RegisterCourtCommand command = new RegisterCourtCommand(
                "1", CourtSection.CANCHA_1, null,
                "cancha.pdf", "application/pdf", 2048L, new ByteArrayInputStream("contenido".getBytes())
        );

        assertThrows(InvalidCourtImageException.class, () -> service.register(command));
    }

    @Test
    void register_imagenSuperaLimite_lanzaInvalidCourtImageException() {
        TournamentRepositoryPort tournamentRepositoryMock = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepositoryMock = mock(CourtRepositoryPort.class);
        CourtImageStoragePort imageStorageMock = mock(CourtImageStoragePort.class);

        when(tournamentRepositoryMock.findById("1")).thenReturn(Optional.of(sampleTournament()));

        RegisterCourtService service = new RegisterCourtService(tournamentRepositoryMock, courtRepositoryMock, imageStorageMock);

        RegisterCourtCommand command = new RegisterCourtCommand(
                "1", CourtSection.CANCHA_1, null,
                "cancha.jpg", "image/jpeg", InvalidCourtImageException.MAX_SIZE_BYTES + 1,
                new ByteArrayInputStream("contenido".getBytes())
        );

        assertThrows(InvalidCourtImageException.class, () -> service.register(command));
    }
}
