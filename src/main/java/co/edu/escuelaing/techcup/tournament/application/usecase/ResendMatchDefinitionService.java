package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.MatchNotScheduledException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ResendMatchDefinitionUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.MatchDefinitionPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.ScheduledMatchRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResendMatchDefinitionService implements ResendMatchDefinitionUseCase {

    private final TournamentRepositoryPort tournamentRepository;
    private final ScheduledMatchRepositoryPort scheduledMatchRepository;
    private final MatchDefinitionPort matchDefinitionPort;


    @Override
    public void resend(UUID matchId) {
        Tournament tournament = tournamentRepository.findByMatchId(matchId)
                .orElseThrow(() -> new MatchupNotFoundException(matchId));

        Match match = tournament.getMatches().stream()
                .filter(m -> m.getMatchId().equals(matchId))
                .findFirst()
                .orElseThrow(() -> new MatchupNotFoundException(matchId));

        ScheduledMatch scheduledMatch = scheduledMatchRepository.findByMatchupId(matchId)
                .orElseThrow(() -> new MatchNotScheduledException(matchId));

        MatchDefinitionPort.MatchDefinition definition =
                MatchDefinitionFactory.build(tournament, match, scheduledMatch);

        try {
            matchDefinitionPort.sendDefinition(definition);
            match.clearDefinitionSyncPending();
        } catch (RuntimeException ex) {
            match.markDefinitionSyncPending();
            tournamentRepository.save(tournament);
            throw ex;
        }
        tournamentRepository.save(tournament);
    }
}
