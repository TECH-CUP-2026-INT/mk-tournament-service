package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidCourtImageException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.CourtImageStoragePort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.CourtRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RegisterCourtUseCase.RegisterCourtCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;

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

    private Tournament sampleTournament(UUID id) {
        return Tournament.builder()
                .id(id).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(TournamentStatus.DRAFT)
                .reconstruct();
    }

    @Test
    void register_sinImagen_guardaYRetornaCourt() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort tournamentRepositoryMock = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepositoryMock = mock(CourtRepositoryPort.class);
        CourtImageStoragePort imageStorageMock = mock(CourtImageStoragePort.class);

        when(tournamentRepositoryMock.findById(id)).thenReturn(Optional.of(sampleTournament(id)));
        when(courtRepositoryMock.save(any(Court.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterCourtService service = new RegisterCourtService(tournamentRepositoryMock, courtRepositoryMock, imageStorageMock);

        RegisterCourtCommand command = new RegisterCourtCommand(
                id, CourtSection.CANCHA_1, "Cancha techada", null, null, null, null
        );

        Court result = service.register(command);

        assertEquals(CourtSection.CANCHA_1, result.getSection());
        assertNull(result.getImageId());
        verify(imageStorageMock, never()).store(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void register_conImagenValida_guardaImagenYCourt() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort tournamentRepositoryMock = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepositoryMock = mock(CourtRepositoryPort.class);
        CourtImageStoragePort imageStorageMock = mock(CourtImageStoragePort.class);

        when(tournamentRepositoryMock.findById(id)).thenReturn(Optional.of(sampleTournament(id)));
        when(imageStorageMock.store(anyString(), anyString(), anyLong(), any())).thenReturn("image-123");
        when(courtRepositoryMock.save(any(Court.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterCourtService service = new RegisterCourtService(tournamentRepositoryMock, courtRepositoryMock, imageStorageMock);

        RegisterCourtCommand command = new RegisterCourtCommand(
                id, CourtSection.CANCHA_2, null,
                "cancha2.jpg", "image/jpeg", 2048L, new ByteArrayInputStream("contenido".getBytes())
        );

        Court result = service.register(command);

        assertEquals("image-123", result.getImageId());
    }

    @Test
    void register_torneoNoExiste_lanzaTournamentNotFoundException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort tournamentRepositoryMock = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepositoryMock = mock(CourtRepositoryPort.class);
        CourtImageStoragePort imageStorageMock = mock(CourtImageStoragePort.class);

        when(tournamentRepositoryMock.findById(id)).thenReturn(Optional.empty());

        RegisterCourtService service = new RegisterCourtService(tournamentRepositoryMock, courtRepositoryMock, imageStorageMock);

        RegisterCourtCommand command = new RegisterCourtCommand(
                id, CourtSection.CANCHA_1, null, null, null, null, null
        );

        assertThrows(TournamentNotFoundException.class, () -> service.register(command));
    }

    @Test
    void register_imagenNoEsJpgOPng_lanzaInvalidCourtImageException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort tournamentRepositoryMock = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepositoryMock = mock(CourtRepositoryPort.class);
        CourtImageStoragePort imageStorageMock = mock(CourtImageStoragePort.class);

        when(tournamentRepositoryMock.findById(id)).thenReturn(Optional.of(sampleTournament(id)));

        RegisterCourtService service = new RegisterCourtService(tournamentRepositoryMock, courtRepositoryMock, imageStorageMock);

        RegisterCourtCommand command = new RegisterCourtCommand(
                id, CourtSection.CANCHA_1, null,
                "cancha.pdf", "application/pdf", 2048L, new ByteArrayInputStream("contenido".getBytes())
        );

        assertThrows(InvalidCourtImageException.class, () -> service.register(command));
    }

    @Test
    void register_imagenSuperaLimite_lanzaInvalidCourtImageException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort tournamentRepositoryMock = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepositoryMock = mock(CourtRepositoryPort.class);
        CourtImageStoragePort imageStorageMock = mock(CourtImageStoragePort.class);

        when(tournamentRepositoryMock.findById(id)).thenReturn(Optional.of(sampleTournament(id)));

        RegisterCourtService service = new RegisterCourtService(tournamentRepositoryMock, courtRepositoryMock, imageStorageMock);

        RegisterCourtCommand command = new RegisterCourtCommand(
                id, CourtSection.CANCHA_1, null,
                "cancha.jpg", "image/jpeg", InvalidCourtImageException.MAX_SIZE_BYTES + 1,
                new ByteArrayInputStream("contenido".getBytes())
        );

        assertThrows(InvalidCourtImageException.class, () -> service.register(command));
    }
}
