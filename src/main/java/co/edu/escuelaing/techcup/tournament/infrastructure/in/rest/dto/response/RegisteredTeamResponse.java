package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "A team registered in a tournament (legacy registration view).")
public record RegisteredTeamResponse(
        @Schema(description = "Team ID.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID teamId,
        @Schema(description = "Team name.", example = "Los Compiladores") String teamName,
        @Schema(description = "Registration status.", example = "APPROVED") RegistrationStatus registrationStatus,
        @Schema(description = "URL of the team's logo image.", example = "https://placeholder.com/teams/3fa85f64-5717-4562-b3fc-2c963f66afa6/logo")
        String logoUrl
) {}
