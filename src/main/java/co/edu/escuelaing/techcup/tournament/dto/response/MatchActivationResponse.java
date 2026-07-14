package co.edu.escuelaing.techcup.tournament.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of inactivating or reactivating a single match.")
public record MatchActivationResponse(
        @Schema(description = "Match ID.", example = "m01") String matchId,
        @Schema(description = "Whether the match is now active.") boolean active,
        @Schema(description = "Human-readable confirmation message.", example = "The match was successfully inactivated")
        String message
) {}
