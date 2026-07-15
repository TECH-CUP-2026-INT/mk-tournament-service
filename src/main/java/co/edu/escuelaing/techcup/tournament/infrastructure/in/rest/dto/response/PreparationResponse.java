package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Readiness state of a tournament to move into the Preparation phase (fixture generation).")
public record PreparationResponse(
        @Schema(description = "Human-readable readiness status.", example = "incomplete") String status,
        @Schema(description = "Whether the tournament already meets every requirement to enter preparation.")
        boolean readyToActivate,
        @Schema(description = "Number of teams currently approved/enrolled.", example = "6") long approvedTeamsCount,
        @Schema(description = "Requirements still missing before preparation can start (empty if ready).")
        List<String> missingRequirements
) {}
