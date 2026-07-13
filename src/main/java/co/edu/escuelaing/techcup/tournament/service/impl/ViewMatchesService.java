package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Match;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewMatchesUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewMatchesService implements ViewMatchesUseCase {

    private final TournamentRepositoryPort tournamentRepository;

    public ViewMatchesService(TournamentRepositoryPort tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public List<Match> getMatches(String tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));
        return tournament.getMatches();
    }
}
