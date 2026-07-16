package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.HistoricalTournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultHistoricalTournamentsServiceTest {

    private TournamentRepositoryPort tournamentRepository;
    private ConsultHistoricalTournamentsService service;

    @BeforeEach
    void setUp() {
        tournamentRepository = mock(TournamentRepositoryPort.class);
        service = new ConsultHistoricalTournamentsService(tournamentRepository);
    }

    @Test
    void findAll_returnsFinishedTournaments() {
        Tournament t1 = new Tournament(UUID.randomUUID(), "Copa ECI 2024", TournamentStatus.FINISHED);
        Tournament t2 = new Tournament(UUID.randomUUID(), "Copa ECI 2025", TournamentStatus.FINISHED);
        when(tournamentRepository.findAllByStatus(TournamentStatus.FINISHED)).thenReturn(List.of(t1, t2));

        List<Tournament> result = service.findAll();

        assertEquals(2, result.size());
        verify(tournamentRepository).findAllByStatus(TournamentStatus.FINISHED);
    }

    @Test
    void findAll_whenNoFinishedTournaments_returnsEmptyList() {
        when(tournamentRepository.findAllByStatus(TournamentStatus.FINISHED)).thenReturn(List.of());

        List<Tournament> result = service.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_whenFinishedTournamentExists_returnsTournament() {
        UUID id = UUID.randomUUID();
        Tournament t1 = new Tournament(id, "Copa ECI 2024", TournamentStatus.FINISHED);
        when(tournamentRepository.findByIdAndStatus(id, TournamentStatus.FINISHED)).thenReturn(Optional.of(t1));

        Tournament result = service.findById(id);

        assertEquals(id, result.getId());
        assertEquals("Copa ECI 2024", result.getName());
    }

    @Test
    void findById_whenTournamentNotFinished_throwsHistoricalTournamentNotFoundException() {
        UUID id = UUID.randomUUID();
        when(tournamentRepository.findByIdAndStatus(id, TournamentStatus.FINISHED)).thenReturn(Optional.empty());

        assertThrows(HistoricalTournamentNotFoundException.class, () -> service.findById(id));
    }
}
