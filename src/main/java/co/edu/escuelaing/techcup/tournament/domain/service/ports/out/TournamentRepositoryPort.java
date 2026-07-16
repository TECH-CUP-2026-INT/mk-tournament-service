package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para el agregado {@link co.edu.escuelaing.techcup.tournament.domain.model.Tournament}.
 */
public interface TournamentRepositoryPort {
    Tournament save(Tournament tournament);
    Optional<Tournament> findById(UUID id);
    void deleteById(UUID id);
    List<Tournament> findAllByStatus(TournamentStatus status);
    Optional<Tournament> findByIdAndStatus(UUID id, TournamentStatus status);
    Optional<Tournament> findByMatchId(UUID matchId);
    List<Tournament> findAllWithReservedEnrollments();
}
