package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.TournamentParticipant;

public interface InactivateUserUseCase {
    TournamentParticipant inactivate(String tournamentId, String userId);
}
