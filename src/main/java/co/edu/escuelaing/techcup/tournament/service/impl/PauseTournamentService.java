package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.PauseTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class PauseTournamentService implements PauseTournamentUseCase {

    private final TournamentRepositoryPort tournamentRepository;

    public PauseTournamentService(TournamentRepositoryPort tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Tournament execute(PauseTournamentCommand command) {
        Tournament tournament = tournamentRepository.findById(command.tournamentId())
                .orElseThrow(() -> new TournamentNotFoundException(command.tournamentId()));

        switch (command.action()) {
            case PAUSE -> tournament.pause();
            case RESUME -> tournament.resume();
        }

        return tournamentRepository.save(tournament);
    }
}
