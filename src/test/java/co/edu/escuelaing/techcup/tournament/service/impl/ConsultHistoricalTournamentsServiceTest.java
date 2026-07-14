package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.HistoricalTournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

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
        Tournament t1 = new Tournament("t1", "Copa ECI 2024", TournamentStatus.FINISHED);
        Tournament t2 = new Tournament("t2", "Copa ECI 2025", TournamentStatus.FINISHED);
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
        Tournament t1 = new Tournament("t1", "Copa ECI 2024", TournamentStatus.FINISHED);
        when(tournamentRepository.findByIdAndStatus("t1", TournamentStatus.FINISHED)).thenReturn(Optional.of(t1));

        Tournament result = service.findById("t1");

        assertEquals("t1", result.getId());
        assertEquals("Copa ECI 2024", result.getName());
    }

    @Test
    void findById_whenTournamentNotFinished_throwsHistoricalTournamentNotFoundException() {
        when(tournamentRepository.findByIdAndStatus("t2", TournamentStatus.FINISHED)).thenReturn(Optional.empty());

        assertThrows(HistoricalTournamentNotFoundException.class, () -> service.findById("t2"));
    }
}
