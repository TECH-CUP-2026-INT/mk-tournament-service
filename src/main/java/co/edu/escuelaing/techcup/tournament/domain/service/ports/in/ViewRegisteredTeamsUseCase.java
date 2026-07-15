package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;

import java.util.List;

public interface ViewRegisteredTeamsUseCase {

    List<TeamRegistration> getTeams(String tournamentId);
}
