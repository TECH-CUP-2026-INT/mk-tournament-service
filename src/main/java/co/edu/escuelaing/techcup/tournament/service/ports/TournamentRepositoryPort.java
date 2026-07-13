package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;

import java.util.List;
import java.util.Optional;

public interface TournamentRepositoryPort {
    Tournament save(Tournament tournament);
    Optional<Tournament> findById(String id);
    void deleteById(String id);
    List<Tournament> findAllByStatus(TournamentStatus status);
    Optional<Tournament> findByIdAndStatus(String id, TournamentStatus status);
    Optional<Tournament> findByMatchId(String matchId);
    List<Tournament> findAllWithReservedEnrollments();
}
