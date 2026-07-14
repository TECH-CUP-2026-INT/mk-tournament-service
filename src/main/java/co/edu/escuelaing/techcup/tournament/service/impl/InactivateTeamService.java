package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateTeamUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class InactivateTeamService implements InactivateTeamUseCase {

    private final TournamentRepositoryPort repository;

    public InactivateTeamService(TournamentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Tournament inactivate(String tournamentId, String teamId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));

        tournament.inactivateTeam(teamId);

        return repository.save(tournament);
    }
}
