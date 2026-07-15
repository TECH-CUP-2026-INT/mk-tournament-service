package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewRegisteredTeamsUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewRegisteredTeamsService implements ViewRegisteredTeamsUseCase {

    private final TournamentRepositoryPort tournamentRepository;


    @Override
    public List<TeamRegistration> getTeams(String tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));
        tournament.assertActive();
        return tournament.getTeams();
    }
}
