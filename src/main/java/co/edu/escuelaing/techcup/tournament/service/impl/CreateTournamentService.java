package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.CreateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
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
