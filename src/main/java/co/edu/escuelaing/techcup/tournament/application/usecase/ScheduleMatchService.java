package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.CourtNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.ScheduleConflictException;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.CourtRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ScheduleMatchUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.ScheduledMatchRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class ScheduleMatchService implements ScheduleMatchUseCase {

    private final TournamentRepositoryPort tournamentRepository;
    private final CourtRepositoryPort courtRepository;
    private final ScheduledMatchRepositoryPort scheduledMatchRepository;

    public ScheduleMatchService(TournamentRepositoryPort tournamentRepository,
                                 CourtRepositoryPort courtRepository,
                                 ScheduledMatchRepositoryPort scheduledMatchRepository) {
        this.tournamentRepository = tournamentRepository;
        this.courtRepository = courtRepository;
        this.scheduledMatchRepository = scheduledMatchRepository;
    }

    @Override
    public ScheduledMatch schedule(ScheduleMatchCommand command) {
        tournamentRepository.findByMatchId(command.matchupId())
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

        return saved;
    }
}
