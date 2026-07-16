package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Result of inactivating or reactivating a single match.")
public record MatchActivationResponse(
        @Schema(description = "Match ID.", example = "550e8400-e29b-41d4-a716-446655440000") UUID matchId,
        @Schema(description = "Whether the match is now active.") boolean active,
        @Schema(description = "Human-readable confirmation message.", example = "The match was successfully inactivated")
        String message
) {}
