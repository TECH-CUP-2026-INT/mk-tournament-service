package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para canchas.
 */
public interface CourtRepositoryPort {
    Court save(Court court);
    Optional<Court> findById(UUID id);
    Optional<Court> findByMatchId(UUID matchId);
    List<Court> findAllByTournamentId(UUID tournamentId);
}
