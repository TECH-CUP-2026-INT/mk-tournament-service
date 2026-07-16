package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewCourtMapUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.CourtRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.ScheduledMatchRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewCourtMapService implements ViewCourtMapUseCase {

    private final TournamentRepositoryPort tournamentRepository;
    private final CourtRepositoryPort courtRepository;
    private final ScheduledMatchRepositoryPort scheduledMatchRepository;

    @Override
    public List<CourtMapEntry> getCourtMap(String tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));

        List<Court> courts = courtRepository.findAllByTournamentId(tournamentId);

        return courts.stream()
                .map(court -> toEntry(tournament, court))
                .toList();
    }

    private CourtMapEntry toEntry(Tournament tournament, Court court) {
        if (court.getMatchId() == null) {
            return new CourtMapEntry(court, null, null);
        }

        Match match = tournament.getMatches().stream()
                .filter(m -> m.getMatchId().equals(court.getMatchId()))
                .findFirst()
                .orElse(null);

        var scheduledMatch = scheduledMatchRepository.findByMatchupId(court.getMatchId()).orElse(null);

        return new CourtMapEntry(court, match, scheduledMatch);
    }
}
