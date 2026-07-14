package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.SanctionType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current state of a sanction applied to a player.")
public record SanctionResponse(
        @Schema(description = "Unique sanction ID.", example = "sanc_abc123") String id,
        @Schema(description = "ID of the sanctioned player.", example = "player_123") String playerId,
        @Schema(description = "Sanction type.", example = "RED_CARD") SanctionType type,
        @Schema(description = "Matches of suspension still remaining.", example = "1") int matchesRemaining,
        @Schema(description = "Whether the sanction is still in effect (matchesRemaining > 0).") boolean active
) {}
