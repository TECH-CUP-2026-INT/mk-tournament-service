package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.DisqualificationReason;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import java.util.UUID;

public interface DisqualifyTeamUseCase {
    Tournament disqualify(UUID tournamentId, UUID teamId, DisqualificationReason reason);
}
