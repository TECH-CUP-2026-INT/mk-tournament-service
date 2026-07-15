package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentParticipant;
import java.util.Optional;

/**
 * Puerto de persistencia para participantes (jugadores) de un torneo.
 */
public interface TournamentParticipantRepositoryPort {
    TournamentParticipant save(TournamentParticipant participant);
    Optional<TournamentParticipant> findByTournamentIdAndUserId(String tournamentId, String userId);
}
