package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.PauseTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PauseTournamentService implements PauseTournamentUseCase {

    private final TournamentRepositoryPort tournamentRepository;


    @Override
    public Tournament execute(PauseTournamentCommand command) {
        Tournament tournament = tournamentRepository.findById(command.tournamentId())
                .orElseThrow(() -> new TournamentNotFoundException(command.tournamentId().toString()));

        switch (command.action()) {
            case PAUSE -> tournament.pause();
            case RESUME -> tournament.resume();
        }

        return tournamentRepository.save(tournament);
    }
}
