package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.ChampionResolution;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con el equipo campeón asignado a un torneo")
public record ChampionResponse(
        @Schema(description = "ID del torneo", example = "abc123") String tournamentId,
        @Schema(description = "ID del equipo campeón", example = "team_xyz789") String championTeamId,
        @Schema(description = "Cómo se resolvió el campeón", example = "REGULATION_TIME") ChampionResolution resolution
) {}
