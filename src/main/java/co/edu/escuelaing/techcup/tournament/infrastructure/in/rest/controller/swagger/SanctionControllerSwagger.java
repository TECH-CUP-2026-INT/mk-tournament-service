package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller.swagger;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.ApplySanctionRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.SanctionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Sanctions", description = "Applying and querying player sanctions")
public interface SanctionControllerSwagger {

    @Operation(summary = "Apply sanction to player",
            description = "Red card = 1 match; 2 yellow cards in different matches = 1 match; 2 yellow cards in the "
                    + "same match = red card; for conduct sanctions, the Organizer defines the number of matches suspended.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sanction applied",
                    content = @Content(schema = @Schema(implementation = SanctionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    ResponseEntity<SanctionResponse> apply(ApplySanctionRequest request);

    @Operation(summary = "Get a player's active sanctions",
            description = "Returns only sanctions still in effect (matchesRemaining > 0); served ones are omitted.")
    @ApiResponse(responseCode = "200", description = "List of the player's active sanctions",
            content = @Content(schema = @Schema(implementation = SanctionResponse.class)))
    ResponseEntity<List<SanctionResponse>> getActiveSanctions(
            @Parameter(description = "Player ID", example = "550e8400-e29b-41d4-a716-446655440000") UUID playerId);

    /**
     * Pending integration point: the future "Finish match" story must invoke
     * this endpoint whenever a match finishes. Today nothing triggers it
     * automatically.
     */
    @Operation(summary = "Record match finished (internal integration point)",
            description = "Pending internal integration: no automatic trigger exists yet. The future \"Finish match\" "
                    + "story in the Match Service must call this endpoint whenever a match finishes, so one match of "
                    + "suspension is served for every sanctioned player with an active sanction.")
    @ApiResponse(responseCode = "200", description = "Match-finished event recorded")
    ResponseEntity<Void> recordMatchFinished();
}
