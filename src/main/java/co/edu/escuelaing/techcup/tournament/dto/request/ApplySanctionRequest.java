package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.SanctionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplySanctionRequest(

        @NotBlank(message = "El id del jugador es obligatorio")
        String playerId,

        @NotNull(message = "El tipo de sanción es obligatorio")
        SanctionType type,

        Integer matchesSuspended
) {}
