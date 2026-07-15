package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateTeamUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InactivateTeamService implements InactivateTeamUseCase {

    private final TournamentRepositoryPort repository;


    @Override
    public Tournament inactivate(String tournamentId, String teamId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));

        tournament.inactivateTeam(teamId);

        return repository.save(tournament);
    }
}
