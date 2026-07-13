package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.TournamentParticipant;
import java.util.Optional;

public interface TournamentParticipantRepositoryPort {
    TournamentParticipant save(TournamentParticipant participant);
    Optional<TournamentParticipant> findByTournamentIdAndUserId(String tournamentId, String userId);
}
