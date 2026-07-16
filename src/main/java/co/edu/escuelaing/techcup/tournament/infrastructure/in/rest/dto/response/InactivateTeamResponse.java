package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Result of inactivating a team within a tournament.")
public record InactivateTeamResponse(
        @Schema(description = "Tournament ID.", example = "550e8400-e29b-41d4-a716-446655440000") UUID tournamentId,
        @Schema(description = "ID of the inactivated team.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID teamId,
        @Schema(description = "Team's new registration status (always INACTIVE).", example = "INACTIVE")
        RegistrationStatus status,
        @Schema(description = "Human-readable confirmation message.", example = "The team was successfully inactivated")
        String message
) {}
