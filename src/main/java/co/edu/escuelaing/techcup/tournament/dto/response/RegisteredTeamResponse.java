package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A team registered in a tournament (legacy registration view).")
public record RegisteredTeamResponse(
        @Schema(description = "Team ID.", example = "team_xyz789") String teamId,
        @Schema(description = "Team name.", example = "Los Compiladores") String teamName,
        @Schema(description = "Registration status.", example = "APPROVED") RegistrationStatus registrationStatus,
        @Schema(description = "URL of the team's logo image.", example = "https://placeholder.com/teams/team_xyz789/logo")
        String logoUrl
) {}
