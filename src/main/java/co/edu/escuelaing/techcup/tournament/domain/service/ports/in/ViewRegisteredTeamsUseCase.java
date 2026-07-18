package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;

import java.util.List;
import java.util.UUID;

public interface ViewRegisteredTeamsUseCase {

    List<TeamRegistration> getTeams(UUID tournamentId);
}
