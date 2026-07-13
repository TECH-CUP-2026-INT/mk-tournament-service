// src/test/java/.../application/service/CreateTournamentServiceTest.java
package co.edu.escuelaing.techcup.tournament.application.service;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import co.edu.escuelaing.techcup.tournament.domain.port.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateTournamentServiceTest {

    @Test
    void create_delegatesToRepositoryAndReturnsSavedTournament() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);

        Tournament newTournament = Tournament.create(
                "Copa Enero", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                null, null
        );

        when(repositoryMock.save(newTournament)).thenReturn(newTournament);

        CreateTournamentService service = new CreateTournamentService(repositoryMock);
        Tournament result = service.create(newTournament);

        assertEquals("Copa Enero", result.getName());
        verify(repositoryMock).save(newTournament);
    }
}