package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia para canchas.
 */
public interface CourtRepositoryPort {
    Court save(Court court);
    Optional<Court> findById(String id);
    Optional<Court> findByMatchId(String matchId);
    List<Court> findAllByTournamentId(String tournamentId);
}
