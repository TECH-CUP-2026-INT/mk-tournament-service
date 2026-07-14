package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.SanctionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = """
        Request to sanction a player. RED_CARD and YELLOW_CARD_ACCUMULATION always suspend 1 match \
        automatically. CONDUCT sanctions require matchesSuspended, since the Organizer decides the \
        number of matches for conduct-related sanctions.""")
public record ApplySanctionRequest(

        @Schema(description = "ID of the sanctioned player.", example = "player_123")
        @NotBlank(message = "El id del jugador es obligatorio")
        String playerId,

        @Schema(description = "Sanction type. RED_CARD and YELLOW_CARD_ACCUMULATION = 1 match; CONDUCT = organizer-defined.",
                example = "RED_CARD")
        @NotNull(message = "El tipo de sanción es obligatorio")
        SanctionType type,

        @Schema(description = "Number of matches suspended. Required (and must be > 0) only when type is CONDUCT; ignored otherwise.",
                example = "2")
        Integer matchesSuspended
) {}
