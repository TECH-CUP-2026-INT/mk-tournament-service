package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.TournamentInactivationAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record InactivateTournamentRequest(
        @Schema(description = "Acción a ejecutar sobre el torneo", example = "INACTIVATE")
        @NotNull TournamentInactivationAction action
) {}
