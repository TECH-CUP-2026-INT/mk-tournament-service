package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentCannotBeDeletedException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.DeleteTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
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
