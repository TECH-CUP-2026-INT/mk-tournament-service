package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import co.edu.escuelaing.techcup.tournament.domain.model.ParticipantStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of inactivating a user's participation within a tournament.")
public record InactivateUserResponse(
        @Schema(description = "Tournament ID.", example = "abc123") String tournamentId,
        @Schema(description = "ID of the inactivated user.", example = "user_123") String userId,
        @Schema(description = "User's new participant status (always INACTIVE).", example = "INACTIVE")
        ParticipantStatus status,
        @Schema(description = "Human-readable confirmation message.", example = "The user was successfully inactivated in the tournament")
        String message
) {}
