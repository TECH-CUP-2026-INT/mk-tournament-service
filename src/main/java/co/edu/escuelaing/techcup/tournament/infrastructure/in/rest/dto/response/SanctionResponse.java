package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Current state of a sanction applied to a player.")
public record SanctionResponse(
        @Schema(description = "Unique sanction ID.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id,
        @Schema(description = "ID of the sanctioned player.", example = "550e8400-e29b-41d4-a716-446655440000") UUID playerId,
        @Schema(description = "Sanction type.", example = "RED_CARD") SanctionType type,
        @Schema(description = "Matches of suspension still remaining.", example = "1") int matchesRemaining,
        @Schema(description = "Whether the sanction is still in effect (matchesRemaining > 0).") boolean active
) {}
