package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;

import java.util.UUID;

public interface EnrollTeamInTournamentUseCase {
    Enrollment enrollTeam(UUID tournamentId, UUID teamId);
}
