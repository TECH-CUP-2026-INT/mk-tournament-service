package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia para el agregado {@link co.edu.escuelaing.techcup.tournament.domain.model.Tournament}.
 */
public interface TournamentRepositoryPort {
    Tournament save(Tournament tournament);
    Optional<Tournament> findById(String id);
    void deleteById(String id);
    List<Tournament> findAllByStatus(TournamentStatus status);
    Optional<Tournament> findByIdAndStatus(String id, TournamentStatus status);
    Optional<Tournament> findByMatchId(String matchId);
    List<Tournament> findAllWithReservedEnrollments();
}
