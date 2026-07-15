package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.UserInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentParticipant;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateUserUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentParticipantRepositoryPort;
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
