package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentParticipant;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para participantes (jugadores) de un torneo.
 */
public interface TournamentParticipantRepositoryPort {
    TournamentParticipant save(TournamentParticipant participant);
    Optional<TournamentParticipant> findByTournamentIdAndUserId(UUID tournamentId, UUID userId);
}
