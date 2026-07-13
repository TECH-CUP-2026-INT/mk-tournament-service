package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.TeamRegistration;

import java.util.List;

public interface ViewRegisteredTeamsUseCase {

    List<TeamRegistration> getTeams(String tournamentId);
}
