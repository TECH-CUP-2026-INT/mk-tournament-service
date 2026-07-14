package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.DisqualificationReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record DisqualifyTeamRequest(
        @Schema(description = "Motivo de la descalificación", example = "RULES_VIOLATION")
        @NotNull(message = "El motivo de descalificación es obligatorio") DisqualificationReason reason
) {}
