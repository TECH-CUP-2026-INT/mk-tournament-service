package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.RemovalReason;
import co.edu.escuelaing.techcup.tournament.service.Tournament;

public interface RemoveTeamUseCase {
    Tournament remove(String tournamentId, String teamId, RemovalReason reason);
}
