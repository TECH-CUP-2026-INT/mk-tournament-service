package co.edu.escuelaing.techcup.tournament.domain.port.out;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import java.util.Optional;

public interface TournamentRepositoryPort {

    Optional<Tournament> findById(String id);

    void deleteById(String id);
}
