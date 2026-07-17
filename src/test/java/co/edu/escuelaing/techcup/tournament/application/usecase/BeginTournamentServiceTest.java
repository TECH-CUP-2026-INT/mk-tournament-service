package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentBeginNotAllowedException;
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

class BeginTournamentServiceTest {

    private Tournament sampleTournament(UUID id, TournamentStatus status) {
        return Tournament.builder()
                .id(id).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(status)
                .reconstruct();
    }

    @Test
    void begin_torneoEnPreparacion_loIniciaYGuarda() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.IN_PREPARATION);

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        BeginTournamentService service = new BeginTournamentService(repositoryMock);
        Tournament result = service.begin(id);

        assertEquals(TournamentStatus.IN_PROGRESS, result.getStatus());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void begin_torneoNoExiste_lanzaTournamentNotFoundException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findById(id)).thenReturn(Optional.empty());

        BeginTournamentService service = new BeginTournamentService(repositoryMock);

        assertThrows(TournamentNotFoundException.class, () -> service.begin(id));
    }

    @Test
    void begin_torneoNoEnPreparacion_lanzaTournamentBeginNotAllowedException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, TournamentStatus.ACTIVE);
        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));

        BeginTournamentService service = new BeginTournamentService(repositoryMock);

        assertThrows(TournamentBeginNotAllowedException.class, () -> service.begin(id));
    }
}
