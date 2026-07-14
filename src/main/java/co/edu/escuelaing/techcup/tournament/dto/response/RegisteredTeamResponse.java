package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;

public record RegisteredTeamResponse(
        String teamId,
        String teamName,
        RegistrationStatus registrationStatus,
        String logoUrl
) {}
