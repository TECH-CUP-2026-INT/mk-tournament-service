package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of inactivating a team within a tournament.")
public record InactivateTeamResponse(
        @Schema(description = "Tournament ID.", example = "abc123") String tournamentId,
        @Schema(description = "ID of the inactivated team.", example = "team_xyz789") String teamId,
        @Schema(description = "Team's new registration status (always INACTIVE).", example = "INACTIVE")
        RegistrationStatus status,
        @Schema(description = "Human-readable confirmation message.", example = "The team was successfully inactivated")
        String message
) {}
