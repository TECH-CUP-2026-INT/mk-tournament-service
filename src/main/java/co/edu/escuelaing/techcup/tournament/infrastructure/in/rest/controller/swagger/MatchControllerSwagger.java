package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller.swagger;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.MatchActivationRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.ScheduleMatchRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.MatchActivationResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.ScheduledMatchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Matches", description = "Scheduling matches and activating/inactivating them")
public interface MatchControllerSwagger {

    @Operation(summary = "Schedule match",
            description = "Assigns a date, time, court and referee to an already generated matchup, creating a new "
                    + "scheduled-match record. Fails with 409 if the court or referee is already booked at that "
                    + "exact date and time.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Match scheduled",
                    content = @Content(schema = @Schema(implementation = ScheduledMatchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "409", description = "Scheduling conflict with the court or the referee", content = @Content)
    })
    ResponseEntity<ScheduledMatchResponse> schedule(ScheduleMatchRequest request);

    @Operation(summary = "Activate or inactivate match",
            description = "An inactive match keeps its previously recorded data (score, status) but blocks new "
                    + "referee events: result, penalty shootout winner, and no-show. Cards, substitutions and clock "
                    + "management are not implemented in this service — they are the responsibility of the future "
                    + "Match Service.")
    @ApiResponse(responseCode = "200", description = "Match activated or inactivated",
            content = @Content(schema = @Schema(implementation = MatchActivationResponse.class)))
    ResponseEntity<MatchActivationResponse> activation(
            @Parameter(description = "Match ID", example = "m01") String matchId,
            MatchActivationRequest request);
}
