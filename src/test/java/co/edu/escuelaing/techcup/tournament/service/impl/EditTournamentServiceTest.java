package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentCannotBeEditedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.ports.EditTournamentUseCase.EditTournamentCommand;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EditTournamentServiceTest {

    private Tournament sampleTournament(TournamentStatus status) {
        return Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                status
        );
    }

    @Test
    void edit_camposValidosEnEstadoActivo_actualizaYGuarda() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        EditTournamentService service = new EditTournamentService(repositoryMock);

        EditTournamentCommand command = new EditTournamentCommand(
                "1", "Copa Febrero", TournamentType.LIGHTNING, TournamentFormat.GROUPS,
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
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(TournamentStatus.IN_PROGRESS);

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        EditTournamentService service = new EditTournamentService(repositoryMock);

        EditTournamentCommand command = new EditTournamentCommand(
                "1", "Copa Febrero", null, null, null, null, null, null, null, null, null
        );

        Tournament result = service.edit(command);

        assertEquals("Copa Febrero", result.getName());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void edit_tipoEnEstadoNoActivo_lanzaTournamentCannotBeEditedException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(TournamentStatus.IN_PROGRESS);
        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));

        EditTournamentService service = new EditTournamentService(repositoryMock);

        EditTournamentCommand command = new EditTournamentCommand(
                "1", null, TournamentType.LIGHTNING, null, null, null, null, null, null, null, null
        );

        assertThrows(TournamentCannotBeEditedException.class, () -> service.edit(command));
    }

    @Test
    void edit_torneoNoExiste_lanzaTournamentNotFoundException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findById("99")).thenReturn(Optional.empty());

        EditTournamentService service = new EditTournamentService(repositoryMock);

        EditTournamentCommand command = new EditTournamentCommand(
                "99", "Nuevo Nombre", null, null, null, null, null, null, null, null, null
        );

        assertThrows(TournamentNotFoundException.class, () -> service.edit(command));
    }

    @Test
    void edit_torneoFinalizado_lanzaTournamentCannotBeEditedException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(TournamentStatus.FINISHED);
        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));

        EditTournamentService service = new EditTournamentService(repositoryMock);

        EditTournamentCommand command = new EditTournamentCommand(
                "1", "Nuevo Nombre", null, null, null, null, null, null, null, null, null
        );

        assertThrows(TournamentCannotBeEditedException.class, () -> service.edit(command));
    }

    @Test
    void edit_nombreVacio_lanzaInvalidTournamentDataException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);
        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));

        EditTournamentService service = new EditTournamentService(repositoryMock);

        EditTournamentCommand command = new EditTournamentCommand(
                "1", " ", null, null, null, null, null, null, null, null, null
        );

        assertThrows(InvalidTournamentDataException.class, () -> service.edit(command));
    }
}
