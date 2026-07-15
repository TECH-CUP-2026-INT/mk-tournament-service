package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;

public interface EnrollTeamInTournamentUseCase {
    Enrollment enrollTeam(String tournamentId, String teamId);
}
