package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.UserInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.service.ParticipantStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentParticipant;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentParticipantRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InactivateUserServiceTest {

    @Mock
    private TournamentParticipantRepositoryPort participantRepository;

    @InjectMocks
    private InactivateUserService inactivateUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void inactivate_whenUserIsParticipating_setsStatusToInactive() {
        TournamentParticipant participant = TournamentParticipant.reconstruct(
                "p-1", "t-1", "user-1", ParticipantStatus.ACTIVE);

        when(participantRepository.findByTournamentIdAndUserId("t-1", "user-1"))
                .thenReturn(Optional.of(participant));
        when(participantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TournamentParticipant result = inactivateUserService.inactivate("t-1", "user-1");

        assertEquals(ParticipantStatus.INACTIVE, result.getStatus());
        verify(participantRepository).save(participant);
    }

    @Test
    void inactivate_whenUserNotParticipating_throwsUserInactivationNotAllowedException() {
        when(participantRepository.findByTournamentIdAndUserId("t-1", "user-99"))
                .thenReturn(Optional.empty());

        assertThrows(UserInactivationNotAllowedException.class,
                () -> inactivateUserService.inactivate("t-1", "user-99"));
        verify(participantRepository, never()).save(any());
    }

    @Test
    void inactivate_whenUserAlreadyInactive_throwsUserInactivationNotAllowedException() {
        TournamentParticipant participant = TournamentParticipant.reconstruct(
                "p-1", "t-1", "user-1", ParticipantStatus.INACTIVE);

        when(participantRepository.findByTournamentIdAndUserId("t-1", "user-1"))
                .thenReturn(Optional.of(participant));

        assertThrows(UserInactivationNotAllowedException.class,
                () -> inactivateUserService.inactivate("t-1", "user-1"));
        verify(participantRepository, never()).save(any());
    }

    @Test
    void inactivate_returnsParticipantWithCorrectTournamentAndUserId() {
        TournamentParticipant participant = TournamentParticipant.reconstruct(
                "p-1", "t-1", "user-1", ParticipantStatus.ACTIVE);

        when(participantRepository.findByTournamentIdAndUserId("t-1", "user-1"))
                .thenReturn(Optional.of(participant));
        when(participantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TournamentParticipant result = inactivateUserService.inactivate("t-1", "user-1");

        assertEquals("t-1", result.getTournamentId());
        assertEquals("user-1", result.getUserId());
    }
}
