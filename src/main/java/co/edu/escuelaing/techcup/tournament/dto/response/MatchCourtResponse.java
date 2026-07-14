package co.edu.escuelaing.techcup.tournament.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Court assigned to a match, or a pending placeholder if no court has been assigned yet.")
public record MatchCourtResponse(
        @Schema(description = "Court ID, or null if pending.", example = "court-1") String courtId,
        @Schema(description = "Match ID.", example = "m01") String matchId,
        @Schema(description = "Campus map section the court is in, or null if pending.", example = "CANCHA_1") String section,
        @Schema(description = "Court description, or null if pending.", example = "Synthetic grass court, north side of campus") String description,
        @Schema(description = "ID of the court's image, or null if pending/none.") String imageId,
        @Schema(description = "Present only when no court has been assigned yet.", example = "No court has been assigned to this match yet")
        String message
) {
    public static MatchCourtResponse pending(String matchId) {
        return new MatchCourtResponse(null, matchId, null, null, null, "No court has been assigned to this match yet");
    }
}
