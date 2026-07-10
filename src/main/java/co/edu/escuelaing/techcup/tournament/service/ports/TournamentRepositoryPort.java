package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Tournament;

import java.util.Optional;

public interface TournamentRepositoryPort {
    Tournament save(Tournament tournament);
    Optional<Tournament> findById(String id);
    void deleteById(String id);
}
