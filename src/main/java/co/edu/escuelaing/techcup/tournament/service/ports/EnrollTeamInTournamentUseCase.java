package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Enrollment;

public interface EnrollTeamInTournamentUseCase {
    Enrollment enrollTeam(String tournamentId, String teamId);
}
