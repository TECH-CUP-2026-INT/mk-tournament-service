package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.UserInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.service.TournamentParticipant;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateUserUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentParticipantRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class InactivateUserService implements InactivateUserUseCase {

    private final TournamentParticipantRepositoryPort participantRepository;

    public InactivateUserService(TournamentParticipantRepositoryPort participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public TournamentParticipant inactivate(String tournamentId, String userId) {
        TournamentParticipant participant = participantRepository
                .findByTournamentIdAndUserId(tournamentId, userId)
                .orElseThrow(() -> new UserInactivationNotAllowedException(
                        "El usuario no está participando en este torneo"));

        if (participant.isInactive()) {
            throw new UserInactivationNotAllowedException("El usuario ya está inactivo en este torneo");
        }

        participant.inactivate();
        return participantRepository.save(participant);
    }
}
