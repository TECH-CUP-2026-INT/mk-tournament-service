package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "One row of a group's standings table.")
public record TeamStandingResponse(
        @Schema(description = "Position within the group (1-based).", example = "1") int position,
        @Schema(description = "Team ID.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID teamId,
        @Schema(description = "Matches played.", example = "3") int played,
        @Schema(description = "Matches won.", example = "2") int won,
        @Schema(description = "Matches drawn.", example = "1") int drawn,
        @Schema(description = "Matches lost.", example = "0") int lost,
        @Schema(description = "Goals for.", example = "5") int goalsFor,
        @Schema(description = "Goals against.", example = "2") int goalsAgainst,
        @Schema(description = "Goal difference.", example = "3") int goalDifference,
        @Schema(description = "Points.", example = "7") int points
) {}
