package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentCannotBeDeletedException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.DeleteTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class DeleteTournamentService implements DeleteTournamentUseCase {

    private final TournamentRepositoryPort repository;

    public DeleteTournamentService(TournamentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void delete(String tournamentId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));

        if (tournament.getStatus() != TournamentStatus.FINISHED) {
            throw new TournamentCannotBeDeletedException(tournamentId, tournament.getStatus());
        }

        repository.deleteById(tournamentId);
    }
}
