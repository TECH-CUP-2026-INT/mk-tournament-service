package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.ChampionResolution;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Champion team assigned to a tournament.")
public record ChampionResponse(
        @Schema(description = "Tournament ID.", example = "abc123") String tournamentId,
        @Schema(description = "ID of the champion team.", example = "team_xyz789") String championTeamId,
        @Schema(description = "How the champion was decided: REGULATION_TIME or PENALTIES.", example = "REGULATION_TIME")
        ChampionResolution resolution
) {}
