package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateMatchUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
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
