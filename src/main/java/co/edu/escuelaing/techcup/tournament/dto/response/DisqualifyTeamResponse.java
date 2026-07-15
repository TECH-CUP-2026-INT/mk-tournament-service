package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of disqualifying a team from a tournament.")
public record DisqualifyTeamResponse(
        @Schema(description = "Tournament ID.", example = "abc123") String tournamentId,
        @Schema(description = "ID of the disqualified team.", example = "team_xyz789") String teamId,
        @Schema(description = "Team's new registration status (always DISQUALIFIED).", example = "DISQUALIFIED")
        RegistrationStatus status,
        @Schema(description = "Human-readable confirmation message.", example = "The team was successfully disqualified")
        String message
) {}
