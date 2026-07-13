package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.DisqualificationReason;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.DisqualifyTeamUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class DisqualifyTeamService implements DisqualifyTeamUseCase {

    private final TournamentRepositoryPort repository;

    public DisqualifyTeamService(TournamentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Tournament disqualify(String tournamentId, String teamId, DisqualificationReason reason) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));

        tournament.disqualifyTeam(teamId, reason);

        return repository.save(tournament);
    }
}
