package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.service.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewRegisteredTeamsServiceTest {

    private TournamentRepositoryPort tournamentRepository;
    private ViewRegisteredTeamsService service;

    @BeforeEach
    void setUp() {
        tournamentRepository = mock(TournamentRepositoryPort.class);
        service = new ViewRegisteredTeamsService(tournamentRepository);
    }

    @Test
    void getTeams_whenTournamentHasTeams_returnsTeamList() {
        Tournament tournament = new Tournament("t1", "Copa ECI", TournamentStatus.ACTIVE);
        tournament.setTeams(List.of(
                new TeamRegistration("team1", "Los Tigres", RegistrationStatus.APPROVED),
                new TeamRegistration("team2", "Los Leones", RegistrationStatus.APPROVED)
        ));
        when(tournamentRepository.findById("t1")).thenReturn(Optional.of(tournament));

        List<TeamRegistration> result = service.getTeams("t1");

        assertEquals(2, result.size());
        assertEquals("Los Tigres", result.get(0).getTeamName());
        assertEquals("Los Leones", result.get(1).getTeamName());
    }

    @Test
    void getTeams_whenNoTeamsRegistered_returnsEmptyList() {
        Tournament tournament = new Tournament("t1", "Copa ECI", TournamentStatus.ACTIVE);
        tournament.setTeams(List.of());
        when(tournamentRepository.findById("t1")).thenReturn(Optional.of(tournament));

        List<TeamRegistration> result = service.getTeams("t1");

        assertTrue(result.isEmpty());
    }

    @Test
    void getTeams_whenTournamentNotFound_throwsTournamentNotFoundException() {
        when(tournamentRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> service.getTeams("unknown"));
    }
}
