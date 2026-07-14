package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.TournamentPauseAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = """
        Request to pause or resume a tournament. Pausing suspends new event registration but keeps \
        all existing data queryable; resuming lifts that suspension.""")
public record PauseTournamentRequest(
        @Schema(description = "Action to apply: PAUSE or RESUME.", example = "PAUSE")
        @NotNull TournamentPauseAction action
) {}
