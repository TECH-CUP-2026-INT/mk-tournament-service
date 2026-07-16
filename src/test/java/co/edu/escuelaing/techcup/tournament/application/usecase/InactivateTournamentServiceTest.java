package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentInactivationAction;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateTournamentUseCase.InactivateTournamentCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InactivateTournamentServiceTest {

    private Tournament sampleTournament(UUID id, TournamentStatus status) {
        return Tournament.builder()
                .id(id).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, 3, 1)).endDate(LocalDate.of(2026, 3, 20))
                .registrationDeadline(LocalDate.of(2026, 2, 20))
                .status(status)
                .reconstruct();
    }

    @Test
    void execute_inactivate_inactivaYGuardaElTorneo() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.ACTIVE);

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        InactivateTournamentService service = new InactivateTournamentService(repositoryMock);

        Tournament result = service.execute(new InactivateTournamentCommand(id, TournamentInactivationAction.INACTIVATE));

        assertFalse(result.isActive());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void execute_reactivate_reactivaYGuardaElTorneo() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.ACTIVE);
        tournament.inactivate();

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        InactivateTournamentService service = new InactivateTournamentService(repositoryMock);

        Tournament result = service.execute(new InactivateTournamentCommand(id, TournamentInactivationAction.REACTIVATE));

        assertTrue(result.isActive());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void execute_torneoNoExiste_lanzaTournamentNotFoundException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findById(id)).thenReturn(Optional.empty());

        InactivateTournamentService service = new InactivateTournamentService(repositoryMock);

        assertThrows(TournamentNotFoundException.class,
                () -> service.execute(new InactivateTournamentCommand(id, TournamentInactivationAction.INACTIVATE)));
    }

    @Test
    void execute_torneoFinalizado_lanzaTournamentInactivationNotAllowedException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.FINISHED);
        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));

        InactivateTournamentService service = new InactivateTournamentService(repositoryMock);

        assertThrows(TournamentInactivationNotAllowedException.class,
                () -> service.execute(new InactivateTournamentCommand(id, TournamentInactivationAction.INACTIVATE)));
    }
}
