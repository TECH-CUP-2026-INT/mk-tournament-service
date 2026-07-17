package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.CourtNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.ScheduleConflictException;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.CourtRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ScheduleMatchUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.MatchDefinitionPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.ScheduledMatchRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleMatchService implements ScheduleMatchUseCase {

    private final TournamentRepositoryPort tournamentRepository;
    private final CourtRepositoryPort courtRepository;
    private final ScheduledMatchRepositoryPort scheduledMatchRepository;
    private final MatchDefinitionPort matchDefinitionPort;


    @Override
    public ScheduledMatch schedule(ScheduleMatchCommand command) {
        Tournament tournament = tournamentRepository.findByMatchId(command.matchupId())
                .orElseThrow(() -> new MatchupNotFoundException(command.matchupId()));

        Court court = courtRepository.findById(command.courtId())
                .orElseThrow(() -> new CourtNotFoundException(command.courtId()));

        if (scheduledMatchRepository.existsConflict(
                command.courtId(), command.refereeId(), command.matchDate(), command.matchTime())) {
            throw new ScheduleConflictException(command.courtId(), command.refereeId());
        }

        ScheduledMatch scheduledMatch = ScheduledMatch.create(
                command.matchupId(), command.courtId(), command.refereeId(),
                command.matchDate(), command.matchTime());
        ScheduledMatch saved = scheduledMatchRepository.save(scheduledMatch);

        court.assignMatch(command.matchupId());
        courtRepository.save(court);

        pushDefinition(tournament, command.matchupId(), saved);

        return saved;
    }

    /**
     * Empuja la definición del partido recién programado a Matches. El
     * agendamiento en sí ya quedó guardado antes de llegar acá y no debe
     * fallar por esto: si el envío falla, el partido queda marcado
     * (Match#markDefinitionSyncPending) para reenvío manual (ver
     * ResendMatchDefinitionService) — la falla ya se registra en el log
     * dentro del adapter, no se vuelve a tragar en silencio aquí.
     */
    private void pushDefinition(Tournament tournament, UUID matchId, ScheduledMatch scheduledMatch) {
        Match match = tournament.getMatches().stream()
                .filter(m -> m.getMatchId().equals(matchId))
                .findFirst()
                .orElseThrow(() -> new MatchupNotFoundException(matchId));

        MatchDefinitionPort.MatchDefinition definition = MatchDefinitionFactory.build(tournament, match, scheduledMatch);
        try {
            matchDefinitionPort.sendDefinition(definition);
            match.clearDefinitionSyncPending();
        } catch (RuntimeException ex) {
            match.markDefinitionSyncPending();
        }
        tournamentRepository.save(tournament);
    }
}
