package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.ChampionResolution;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Champion team assigned to a tournament.")
public record ChampionResponse(
        @Schema(description = "Tournament ID.", example = "550e8400-e29b-41d4-a716-446655440000") UUID tournamentId,
        @Schema(description = "ID of the champion team.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID championTeamId,
        @Schema(description = "ID of the runner-up team.", example = "6b4f8e12-1234-4562-b3fc-2c963f66afa6") UUID runnerUpTeamId,
        @Schema(description = "How the champion was decided: REGULATION_TIME or PENALTIES.", example = "REGULATION_TIME")
        ChampionResolution resolution
) {}
