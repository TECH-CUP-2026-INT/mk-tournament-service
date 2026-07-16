package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = """
        One entry of the tournament fixture. homeTeamId/awayTeamId are null for future slots not \
        yet resolved (e.g. an unplayed bracket round) — use displayHomeTeam()/displayAwayTeam() to \
        render "To be defined" for those.""")
public record MatchupResponse(
        @Schema(description = "Match ID.", example = "550e8400-e29b-41d4-a716-446655440000") UUID matchId,
        @Schema(description = "ID of the home team, or null if not yet resolved.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID homeTeamId,
        @Schema(description = "ID of the away team, or null if not yet resolved.", example = "6f9619ff-8b86-d011-b42d-00cf4fc964ff") UUID awayTeamId,
        @Schema(description = "Match status.", example = "PENDING") MatchStatus status,
        @Schema(description = "Home team score in regulation time.", example = "0") int homeScore,
        @Schema(description = "Away team score in regulation time.", example = "0") int awayScore,
        @Schema(description = "Whether this is the tournament's final match.") boolean finalMatch
) {
    private static final String PENDING_SLOT = "To be defined";

    public String displayHomeTeam() {
        return homeTeamId != null ? homeTeamId.toString() : PENDING_SLOT;
    }

    public String displayAwayTeam() {
        return awayTeamId != null ? awayTeamId.toString() : PENDING_SLOT;
    }
}
