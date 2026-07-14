package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.TournamentPauseAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PauseTournamentRequest(
        @Schema(description = "Acción a ejecutar sobre el torneo", example = "PAUSE")
        @NotNull TournamentPauseAction action
) {}
