package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewMatchupsUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewMatchupsService implements ViewMatchupsUseCase {

    private final TournamentRepositoryPort tournamentRepository;

    public ViewMatchupsService(TournamentRepositoryPort tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public List<Match> getMatchups(String tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));
        tournament.assertActive();
        return tournament.getMatches();
    }
}
