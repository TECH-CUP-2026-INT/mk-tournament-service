package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.MatchStatus;

public record MatchResponse(
        String matchId,
        String homeTeamId,
        String awayTeamId,
        MatchStatus status
) {}
