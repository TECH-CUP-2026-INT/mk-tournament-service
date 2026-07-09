package co.edu.escuelaing.techcup.tournament.domain.port.in;

import co.edu.escuelaing.techcup.tournament.domain.model.RemovalReason;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

public interface RemoveTeamUseCase {
    Tournament remove(String tournamentId, String teamId, RemovalReason reason);
}
