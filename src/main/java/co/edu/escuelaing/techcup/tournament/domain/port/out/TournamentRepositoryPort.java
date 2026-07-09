// domain/port/out/TournamentRepositoryPort.java
package co.edu.escuelaing.techcup.tournament.domain.port.out;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import java.util.Optional;

public interface TournamentRepositoryPort {
    Tournament save(Tournament tournament);
    Optional<Tournament> findById(String id);
}