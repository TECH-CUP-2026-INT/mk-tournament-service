package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentParticipant;

public interface InactivateUserUseCase {
    TournamentParticipant inactivate(String tournamentId, String userId);
}
