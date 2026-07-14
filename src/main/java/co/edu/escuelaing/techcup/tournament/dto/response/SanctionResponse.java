package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.SanctionType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con el estado de una sanción aplicada a un jugador")
public record SanctionResponse(
        @Schema(description = "ID único de la sanción", example = "sanc_abc123") String id,
        @Schema(description = "ID del jugador sancionado", example = "player_123") String playerId,
        @Schema(description = "Tipo de sanción", example = "RED_CARD") SanctionType type,
        @Schema(description = "Partidos de suspensión restantes", example = "1") int matchesRemaining,
        @Schema(description = "Indica si la sanción sigue vigente") boolean active
) {}
