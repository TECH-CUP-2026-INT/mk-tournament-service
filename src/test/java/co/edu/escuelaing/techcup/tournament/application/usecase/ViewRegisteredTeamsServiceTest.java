package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;
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
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament(tournamentId, "Copa ECI", TournamentStatus.ACTIVE);
        tournament.setTeams(List.of(
                new TeamRegistration(UUID.randomUUID(), "Los Tigres", RegistrationStatus.APPROVED),
                new TeamRegistration(UUID.randomUUID(), "Los Leones", RegistrationStatus.APPROVED)
        ));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        List<TeamRegistration> result = service.getTeams(tournamentId);

        assertEquals(2, result.size());
        assertEquals("Los Tigres", result.get(0).getTeamName());
        assertEquals("Los Leones", result.get(1).getTeamName());
    }

    @Test
    void getTeams_whenNoTeamsRegistered_returnsEmptyList() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament(tournamentId, "Copa ECI", TournamentStatus.ACTIVE);
        tournament.setTeams(List.of());
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        List<TeamRegistration> result = service.getTeams(tournamentId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getTeams_whenTournamentNotFound_throwsTournamentNotFoundException() {
        UUID unknown = UUID.randomUUID();
        when(tournamentRepository.findById(unknown)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> service.getTeams(unknown));
    }
}
