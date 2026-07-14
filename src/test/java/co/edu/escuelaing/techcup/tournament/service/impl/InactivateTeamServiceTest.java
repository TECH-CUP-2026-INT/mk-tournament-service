package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TeamInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.service.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Tournament tournament = new Tournament("t-1", "MK Cup", TournamentStatus.ACTIVE);
        TeamRegistration team = new TeamRegistration("team-1", "Team Alpha", RegistrationStatus.APPROVED);
        tournament.setTeams(new ArrayList<>(List.of(team)));

        when(repository.findById("t-1")).thenReturn(Optional.of(tournament));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Tournament result = inactivateTeamService.inactivate("t-1", "team-1");

        TeamRegistration updated = result.getTeams().stream()
                .filter(t -> t.getTeamId().equals("team-1"))
                .findFirst().orElseThrow();

        assertEquals(RegistrationStatus.INACTIVE, updated.getRegistrationStatus());
        verify(repository).save(tournament);
    }

    @Test
    void inactivate_whenTournamentNotFound_throwsTournamentNotFoundException() {
        when(repository.findById("t-99")).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class,
                () -> inactivateTeamService.inactivate("t-99", "team-1"));
        verify(repository, never()).save(any());
    }

    @Test
    void inactivate_whenTeamNotEnrolled_throwsTeamInactivationNotAllowedException() {
        Tournament tournament = new Tournament("t-1", "MK Cup", TournamentStatus.ACTIVE);
        tournament.setTeams(new ArrayList<>());

        when(repository.findById("t-1")).thenReturn(Optional.of(tournament));

        assertThrows(TeamInactivationNotAllowedException.class,
                () -> inactivateTeamService.inactivate("t-1", "team-99"));
        verify(repository, never()).save(any());
    }

    @Test
    void inactivate_whenTeamAlreadyInactive_throwsTeamInactivationNotAllowedException() {
        Tournament tournament = new Tournament("t-1", "MK Cup", TournamentStatus.ACTIVE);
        TeamRegistration team = new TeamRegistration("team-1", "Team Alpha", RegistrationStatus.INACTIVE);
        tournament.setTeams(new ArrayList<>(List.of(team)));

        when(repository.findById("t-1")).thenReturn(Optional.of(tournament));

        assertThrows(TeamInactivationNotAllowedException.class,
                () -> inactivateTeamService.inactivate("t-1", "team-1"));
        verify(repository, never()).save(any());
    }
}
