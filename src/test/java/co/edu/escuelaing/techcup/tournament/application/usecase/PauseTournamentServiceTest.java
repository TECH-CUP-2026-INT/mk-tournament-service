package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentPauseNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentPauseAction;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.PauseTournamentUseCase.PauseTournamentCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PauseTournamentServiceTest {

    private Tournament sampleTournament(UUID id, TournamentStatus status) {
        return Tournament.builder()
                .id(id).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(status)
                .reconstruct();
    }

    @Test
    void execute_pause_pausaYGuardaElTorneo() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.ACTIVE);

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        PauseTournamentService service = new PauseTournamentService(repositoryMock);

        Tournament result = service.execute(new PauseTournamentCommand(id, TournamentPauseAction.PAUSE));

        assertTrue(result.isPaused());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void execute_resume_reanudaYGuardaElTorneo() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.ACTIVE);
        tournament.pause();

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        PauseTournamentService service = new PauseTournamentService(repositoryMock);

        Tournament result = service.execute(new PauseTournamentCommand(id, TournamentPauseAction.RESUME));

        assertFalse(result.isPaused());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void execute_torneoNoExiste_lanzaTournamentNotFoundException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findById(id)).thenReturn(Optional.empty());

        PauseTournamentService service = new PauseTournamentService(repositoryMock);

        PauseTournamentCommand command = new PauseTournamentCommand(id, TournamentPauseAction.PAUSE);
        assertThrows(TournamentNotFoundException.class, () -> service.execute(command));
    }

    @Test
    void execute_torneoFinalizado_lanzaTournamentPauseNotAllowedException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.FINISHED);
        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));

        PauseTournamentService service = new PauseTournamentService(repositoryMock);

        PauseTournamentCommand command = new PauseTournamentCommand(id, TournamentPauseAction.PAUSE);
        assertThrows(TournamentPauseNotAllowedException.class, () -> service.execute(command));
    }
}
