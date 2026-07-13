package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class InactivateTournamentService implements InactivateTournamentUseCase {

    private final TournamentRepositoryPort tournamentRepository;

    public InactivateTournamentService(TournamentRepositoryPort tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Tournament execute(InactivateTournamentCommand command) {
        Tournament tournament = tournamentRepository.findById(command.tournamentId())
                .orElseThrow(() -> new TournamentNotFoundException(command.tournamentId()));

        switch (command.action()) {
            case INACTIVATE -> tournament.inactivate();
            case REACTIVATE -> tournament.reactivate();
        }

        return tournamentRepository.save(tournament);
    }
}
