package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.SanctionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplySanctionRequest(

        @Schema(description = "ID del jugador sancionado", example = "player_123")
        @NotBlank(message = "El id del jugador es obligatorio")
        String playerId,

        @Schema(description = "Tipo de sanción", example = "RED_CARD")
        @NotNull(message = "El tipo de sanción es obligatorio")
        SanctionType type,

        @Schema(description = "Cantidad de partidos de suspensión (obligatorio solo para sanciones por CONDUCT)", example = "2")
        Integer matchesSuspended
) {}
