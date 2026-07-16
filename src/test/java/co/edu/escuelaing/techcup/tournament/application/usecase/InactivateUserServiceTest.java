package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.UserInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.model.ParticipantStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentParticipant;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentParticipantRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

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
        UUID tournamentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TournamentParticipant participant = TournamentParticipant.reconstruct(
                UUID.randomUUID(), tournamentId, userId, ParticipantStatus.ACTIVE);

        when(participantRepository.findByTournamentIdAndUserId(tournamentId, userId))
                .thenReturn(Optional.of(participant));
        when(participantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TournamentParticipant result = inactivateUserService.inactivate(tournamentId, userId);

        assertEquals(ParticipantStatus.INACTIVE, result.getStatus());
        verify(participantRepository).save(participant);
    }

    @Test
    void inactivate_whenUserNotParticipating_throwsUserInactivationNotAllowedException() {
        UUID tournamentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(participantRepository.findByTournamentIdAndUserId(tournamentId, userId))
                .thenReturn(Optional.empty());

        assertThrows(UserInactivationNotAllowedException.class,
                () -> inactivateUserService.inactivate(tournamentId, userId));
        verify(participantRepository, never()).save(any());
    }

    @Test
    void inactivate_whenUserAlreadyInactive_throwsUserInactivationNotAllowedException() {
        UUID tournamentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TournamentParticipant participant = TournamentParticipant.reconstruct(
                UUID.randomUUID(), tournamentId, userId, ParticipantStatus.INACTIVE);

        when(participantRepository.findByTournamentIdAndUserId(tournamentId, userId))
                .thenReturn(Optional.of(participant));

        assertThrows(UserInactivationNotAllowedException.class,
                () -> inactivateUserService.inactivate(tournamentId, userId));
        verify(participantRepository, never()).save(any());
    }

    @Test
    void inactivate_returnsParticipantWithCorrectTournamentAndUserId() {
        UUID tournamentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TournamentParticipant participant = TournamentParticipant.reconstruct(
                UUID.randomUUID(), tournamentId, userId, ParticipantStatus.ACTIVE);

        when(participantRepository.findByTournamentIdAndUserId(tournamentId, userId))
                .thenReturn(Optional.of(participant));
        when(participantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TournamentParticipant result = inactivateUserService.inactivate(tournamentId, userId);

        assertEquals(tournamentId, result.getTournamentId());
        assertEquals(userId, result.getUserId());
    }
}
