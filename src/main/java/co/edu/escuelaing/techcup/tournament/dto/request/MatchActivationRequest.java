package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.MatchActivationAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record MatchActivationRequest(
        @Schema(description = "Acción a ejecutar sobre el partido", example = "INACTIVATE")
        @NotNull MatchActivationAction action
) {}
