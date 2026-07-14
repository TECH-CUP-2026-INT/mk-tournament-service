package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Court;
import java.util.Optional;

public interface CourtRepositoryPort {
    Court save(Court court);
    Optional<Court> findById(String id);
    Optional<Court> findByMatchId(String matchId);
}
