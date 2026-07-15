package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

public interface InactivateTeamUseCase {
    Tournament inactivate(String tournamentId, String teamId);
}
