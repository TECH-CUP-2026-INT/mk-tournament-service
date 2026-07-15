package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.DisqualificationReason;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

public interface DisqualifyTeamUseCase {
    Tournament disqualify(String tournamentId, String teamId, DisqualificationReason reason);
}
