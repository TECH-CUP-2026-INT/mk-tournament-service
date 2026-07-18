package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.RemovalReason;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import java.util.UUID;

public interface RemoveTeamUseCase {
    Tournament remove(UUID tournamentId, UUID teamId, RemovalReason reason);
}
