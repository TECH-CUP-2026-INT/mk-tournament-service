package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentParticipant;

import java.util.UUID;

public interface InactivateUserUseCase {
    TournamentParticipant inactivate(UUID tournamentId, UUID userId);
}
