package co.edu.escuelaing.techcup.tournament.application.service;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotDraftException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.port.in.DeleteTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.port.out.TournamentRepositoryPort;
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

        if (!tournament.isDraft()) {
            throw new TournamentNotDraftException(tournamentId, tournament.getStatus());
        }

        repository.deleteById(tournamentId);
    }
}
