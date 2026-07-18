package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.CreateTournamentUseCase.CreateTournamentCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateTournamentServiceTest {

    @Test
    void create_construyeElTorneoDeDominioYLoDelegaAlRepositorio() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CreateTournamentCommand command = new CreateTournamentCommand(
                "Copa Enero", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, Month.MARCH, 1), LocalDate.of(2026, Month.MARCH, 20), LocalDate.of(2026, Month.FEBRUARY, 20),
                null, null
        );

        CreateTournamentService service = new CreateTournamentService(repositoryMock);
        Tournament result = service.create(command);

        assertEquals("Copa Enero", result.getName());
        assertEquals(TournamentType.NORMAL, result.getType());
        verify(repositoryMock).save(any(Tournament.class));
    }
}