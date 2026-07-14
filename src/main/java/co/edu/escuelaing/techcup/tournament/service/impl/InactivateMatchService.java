package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Match;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateMatchUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class InactivateMatchService implements InactivateMatchUseCase {

    private final TournamentRepositoryPort tournamentRepository;

    public InactivateMatchService(TournamentRepositoryPort tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Match execute(InactivateMatchCommand command) {
        Tournament tournament = tournamentRepository.findByMatchId(command.matchId())
                .orElseThrow(() -> new MatchupNotFoundException(command.matchId()));

        Match match = tournament.getMatches().stream()
                .filter(m -> m.getMatchId().equals(command.matchId()))
                .findFirst()
                .orElseThrow(() -> new MatchupNotFoundException(command.matchId()));

        switch (command.action()) {
            case INACTIVATE -> match.inactivate();
            case REACTIVATE -> match.reactivate();
        }

        tournamentRepository.save(tournament);
        return match;
    }
}
