package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        One entry of the tournament fixture. homeTeamId/awayTeamId are null for future slots not \
        yet resolved (e.g. an unplayed bracket round) — use displayHomeTeam()/displayAwayTeam() to \
        render "To be defined" for those.""")
public record MatchupResponse(
        @Schema(description = "Match ID.", example = "m01") String matchId,
        @Schema(description = "ID of the home team, or null if not yet resolved.", example = "team_xyz789") String homeTeamId,
        @Schema(description = "ID of the away team, or null if not yet resolved.", example = "team_abc456") String awayTeamId,
        @Schema(description = "Match status.", example = "PENDING") MatchStatus status,
        @Schema(description = "Home team score in regulation time.", example = "0") int homeScore,
        @Schema(description = "Away team score in regulation time.", example = "0") int awayScore,
        @Schema(description = "Whether this is the tournament's final match.") boolean finalMatch
) {
    private static final String PENDING_SLOT = "To be defined";

    public String displayHomeTeam() {
        return homeTeamId != null ? homeTeamId : PENDING_SLOT;
    }

    public String displayAwayTeam() {
        return awayTeamId != null ? awayTeamId : PENDING_SLOT;
    }
}
