package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.model.RemovalReason;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RemoveTeamUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
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
