package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.MatchActivationAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = """
        Request to inactivate or reactivate a single match. An inactive match keeps its previously \
        recorded data (score, status) but blocks new referee events (result, penalty shootout winner, \
        no-show) until it is reactivated.""")
public record MatchActivationRequest(
        @Schema(description = "Action to apply: INACTIVATE or REACTIVATE.", example = "INACTIVATE")
        @NotNull MatchActivationAction action
) {}
