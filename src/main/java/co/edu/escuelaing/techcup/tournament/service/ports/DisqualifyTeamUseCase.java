package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.DisqualificationReason;
import co.edu.escuelaing.techcup.tournament.service.Tournament;

public interface DisqualifyTeamUseCase {
    Tournament disqualify(String tournamentId, String teamId, DisqualificationReason reason);
}
