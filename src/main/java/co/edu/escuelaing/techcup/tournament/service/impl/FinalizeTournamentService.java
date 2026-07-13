package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.FinalizeTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FinalizeTournamentService implements FinalizeTournamentUseCase {

    private final TournamentRepositoryPort repository;

    public FinalizeTournamentService(TournamentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Tournament finalizeTournament(String tournamentId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(
                        "No se encontro un torneo con id " + tournamentId));

        tournament.finish(LocalDate.now());

        return repository.save(tournament);
    }
}
