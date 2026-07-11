package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.ChampionResolution;

public record ChampionResponse(
        String tournamentId,
        String championTeamId,
        ChampionResolution resolution
) {}
