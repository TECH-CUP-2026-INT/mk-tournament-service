package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request;

import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = """
        Request to sanction a player. RED_CARD and YELLOW_CARD_ACCUMULATION always suspend 1 match \
        automatically. CONDUCT sanctions require matchesSuspended, since the Organizer decides the \
        number of matches for conduct-related sanctions.""")
public record ApplySanctionRequest(

        @Schema(description = "ID of the sanctioned player.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull(message = "El id del jugador es obligatorio")
        UUID playerId,

        @Schema(description = "Sanction type. RED_CARD and YELLOW_CARD_ACCUMULATION = 1 match; CONDUCT = organizer-defined.",
                example = "RED_CARD")
        @NotNull(message = "El tipo de sanción es obligatorio")
        SanctionType type,

        @Schema(description = "Number of matches suspended. Required (and must be > 0) only when type is CONDUCT; ignored otherwise.",
                example = "2")
        Integer matchesSuspended
) {}
