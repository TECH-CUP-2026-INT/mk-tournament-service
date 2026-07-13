package co.edu.escuelaing.techcup.tournament.dto.response;

public record MatchCourtResponse(
        String courtId,
        String matchId,
        String section,
        String description,
        String imageId,
        String message
) {
    public static MatchCourtResponse pending(String matchId) {
        return new MatchCourtResponse(null, matchId, null, null, null, "No court has been assigned to this match yet");
    }
}
