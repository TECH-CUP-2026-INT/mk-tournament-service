package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.MatchStatus;

public record MatchupResponse(
        String matchId,
        String homeTeamId,
        String awayTeamId,
        MatchStatus status,
        int homeScore,
        int awayScore,
        boolean finalMatch
) {
    private static final String PENDING_SLOT = "To be defined";

    public String displayHomeTeam() {
        return homeTeamId != null ? homeTeamId : PENDING_SLOT;
    }

    public String displayAwayTeam() {
        return awayTeamId != null ? awayTeamId : PENDING_SLOT;
    }
}
