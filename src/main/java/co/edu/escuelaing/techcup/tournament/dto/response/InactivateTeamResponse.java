package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;

public record InactivateTeamResponse(
        String tournamentId,
        String teamId,
        RegistrationStatus status,
        String message
) {}
