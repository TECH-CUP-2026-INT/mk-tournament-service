package co.edu.escuelaing.techcup.tournament.application.service;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.port.in.CreateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.port.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateTournamentService implements CreateTournamentUseCase {

    private final TournamentRepositoryPort repository;

    public CreateTournamentService(TournamentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Tournament create(Tournament newTournament) {
        return repository.save(newTournament);
    }
}
