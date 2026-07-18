package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TeamInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InactivateTeamServiceTest {

    @Mock
    private TournamentRepositoryPort repository;

    @InjectMocks
    private InactivateTeamService inactivateTeamService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void inactivate_whenTeamIsEnrolled_setsStatusToInactive() {
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        Tournament tournament = new Tournament(tournamentId, "MK Cup", TournamentStatus.ACTIVE);
        TeamRegistration team = new TeamRegistration(teamId, "Team Alpha", RegistrationStatus.APPROVED);
        tournament.setTeams(new ArrayList<>(List.of(team)));

        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Tournament result = inactivateTeamService.inactivate(tournamentId, teamId);

        TeamRegistration updated = result.getTeams().stream()
                .filter(t -> t.getTeamId().equals(teamId))
                .findFirst().orElseThrow();

        assertEquals(RegistrationStatus.INACTIVE, updated.getRegistrationStatus());
        verify(repository).save(tournament);
    }

    @Test
    void inactivate_whenTournamentNotFound_throwsTournamentNotFoundException() {
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        when(repository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class,
                () -> inactivateTeamService.inactivate(tournamentId, teamId));
        verify(repository, never()).save(any());
    }

    @Test
    void inactivate_whenTeamNotEnrolled_throwsTeamInactivationNotAllowedException() {
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        Tournament tournament = new Tournament(tournamentId, "MK Cup", TournamentStatus.ACTIVE);
        tournament.setTeams(new ArrayList<>());

        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        assertThrows(TeamInactivationNotAllowedException.class,
                () -> inactivateTeamService.inactivate(tournamentId, teamId));
        verify(repository, never()).save(any());
    }

    @Test
    void inactivate_whenTeamAlreadyInactive_throwsTeamInactivationNotAllowedException() {
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        Tournament tournament = new Tournament(tournamentId, "MK Cup", TournamentStatus.ACTIVE);
        TeamRegistration team = new TeamRegistration(teamId, "Team Alpha", RegistrationStatus.INACTIVE);
        tournament.setTeams(new ArrayList<>(List.of(team)));

        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        assertThrows(TeamInactivationNotAllowedException.class,
                () -> inactivateTeamService.inactivate(tournamentId, teamId));
        verify(repository, never()).save(any());
    }
}
