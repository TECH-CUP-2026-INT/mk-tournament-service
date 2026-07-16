package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentCannotBeEditedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.EditTournamentUseCase.EditTournamentCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EditTournamentServiceTest {

    private Tournament sampleTournament(UUID id, TournamentStatus status) {
        return Tournament.builder()
                .id(id).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(status)
                .reconstruct();
    }

    @Test
    void edit_camposValidosEnEstadoActivo_actualizaYGuarda() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.ACTIVE);

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        EditTournamentService service = new EditTournamentService(repositoryMock);

        EditTournamentCommand command = new EditTournamentCommand(
                id, "Copa Febrero", TournamentType.LIGHTNING, TournamentFormat.GROUPS,
                null, null, null, null, null, LocalTime.of(9, 0), LocalTime.of(18, 0)
        );

        Tournament result = service.edit(command);

        assertEquals("Copa Febrero", result.getName());
        assertEquals(TournamentType.LIGHTNING, result.getType());
        assertEquals(TournamentFormat.GROUPS, result.getFormat());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void edit_nombreEnEstadoNoActivoSinTocarTipo_actualizaYGuarda() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.IN_PROGRESS);

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        EditTournamentService service = new EditTournamentService(repositoryMock);

        EditTournamentCommand command = new EditTournamentCommand(
                id, "Copa Febrero", null, null, null, null, null, null, null, null, null
        );

        Tournament result = service.edit(command);

        assertEquals("Copa Febrero", result.getName());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void edit_tipoEnEstadoNoActivo_lanzaTournamentCannotBeEditedException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.IN_PROGRESS);
        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));

        EditTournamentService service = new EditTournamentService(repositoryMock);

        EditTournamentCommand command = new EditTournamentCommand(
                id, null, TournamentType.LIGHTNING, null, null, null, null, null, null, null, null
        );

        assertThrows(TournamentCannotBeEditedException.class, () -> service.edit(command));
    }

    @Test
    void edit_torneoNoExiste_lanzaTournamentNotFoundException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findById(id)).thenReturn(Optional.empty());

        EditTournamentService service = new EditTournamentService(repositoryMock);

        EditTournamentCommand command = new EditTournamentCommand(
                id, "Nuevo Nombre", null, null, null, null, null, null, null, null, null
        );

        assertThrows(TournamentNotFoundException.class, () -> service.edit(command));
    }

    @Test
    void edit_torneoFinalizado_lanzaTournamentCannotBeEditedException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.FINISHED);
        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));

        EditTournamentService service = new EditTournamentService(repositoryMock);

        EditTournamentCommand command = new EditTournamentCommand(
                id, "Nuevo Nombre", null, null, null, null, null, null, null, null, null
        );

        assertThrows(TournamentCannotBeEditedException.class, () -> service.edit(command));
    }

    @Test
    void edit_nombreVacio_lanzaInvalidTournamentDataException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.ACTIVE);
        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));

        EditTournamentService service = new EditTournamentService(repositoryMock);

        EditTournamentCommand command = new EditTournamentCommand(
                id, " ", null, null, null, null, null, null, null, null, null
        );

        assertThrows(InvalidTournamentDataException.class, () -> service.edit(command));
    }
}
