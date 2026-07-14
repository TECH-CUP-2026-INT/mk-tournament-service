package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewRegisteredTeamsUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewRegisteredTeamsService implements ViewRegisteredTeamsUseCase {

    private final TournamentRepositoryPort tournamentRepository;

    public ViewRegisteredTeamsService(TournamentRepositoryPort tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public List<TeamRegistration> getTeams(String tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));
        tournament.assertActive();
        return tournament.getTeams();
    }
}
