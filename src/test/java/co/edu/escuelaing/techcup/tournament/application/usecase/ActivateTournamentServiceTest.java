package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentActivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActivateTournamentServiceTest {

    private Tournament sampleTournament(UUID id, TournamentStatus status) {
        return Tournament.builder()
                .id(id).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(status)
                .reconstruct();
    }

    @Test
    void activate_torneoEnBorrador_loActivaYGuarda() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.DRAFT);

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        ActivateTournamentService service = new ActivateTournamentService(repositoryMock);
        Tournament result = service.activate(id);

        assertEquals(TournamentStatus.ACTIVE, result.getStatus());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void activate_torneoNoExiste_lanzaTournamentNotFoundException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findById(id)).thenReturn(Optional.empty());

        ActivateTournamentService service = new ActivateTournamentService(repositoryMock);

        assertThrows(TournamentNotFoundException.class, () -> service.activate(id));
    }

    @Test
    void activate_torneoYaActivo_lanzaTournamentActivationNotAllowedException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.ACTIVE);
        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));

        ActivateTournamentService service = new ActivateTournamentService(repositoryMock);

        assertThrows(TournamentActivationNotAllowedException.class, () -> service.activate(id));
    }
}
