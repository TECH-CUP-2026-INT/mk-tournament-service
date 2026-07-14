package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.service.RemovalReason;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.RemoveTeamUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class RemoveTeamService implements RemoveTeamUseCase {

    private final TournamentRepositoryPort repository;

    public RemoveTeamService(TournamentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Tournament remove(String tournamentId, String teamId, RemovalReason reason) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + tournamentId));
        tournament.removeTeam(teamId, reason);
        return repository.save(tournament);
    }
}
