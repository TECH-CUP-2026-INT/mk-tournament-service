package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.ParticipantStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Result of inactivating a user's participation within a tournament.")
public record InactivateUserResponse(
        @Schema(description = "Tournament ID.", example = "550e8400-e29b-41d4-a716-446655440000") UUID tournamentId,
        @Schema(description = "ID of the inactivated user.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID userId,
        @Schema(description = "User's new participant status (always INACTIVE).", example = "INACTIVE")
        ParticipantStatus status,
        @Schema(description = "Human-readable confirmation message.", example = "The user was successfully inactivated in the tournament")
        String message
) {}
