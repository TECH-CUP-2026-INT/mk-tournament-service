package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.MatchActivationRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.ScheduleMatchRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.MatchActivationResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.ScheduledMatchResponse;
import co.edu.escuelaing.techcup.tournament.application.mapper.ScheduledMatchRestMapper;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateMatchUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateMatchUseCase.InactivateMatchCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ScheduleMatchUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ScheduleMatchUseCase.ScheduleMatchCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
@Tag(name = "Matches", description = "Scheduling matches and activating/inactivating them")
public class MatchController {

    private final ScheduleMatchUseCase scheduleMatchUseCase;
    private final InactivateMatchUseCase inactivateMatchUseCase;
    private final ScheduledMatchRestMapper mapper;



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
    @PostMapping
    public ResponseEntity<ScheduledMatchResponse> schedule(@Valid @RequestBody ScheduleMatchRequest request) {
        ScheduledMatch scheduled = scheduleMatchUseCase.schedule(new ScheduleMatchCommand(
                request.matchupId(), request.matchDate(), request.matchTime(),
                request.courtId(), request.refereeId()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(scheduled));
    }

    @Operation(summary = "Activate or inactivate match",
            description = "An inactive match keeps its previously recorded data (score, status) but blocks new "
                    + "referee events: result, penalty shootout winner, and no-show. Cards, substitutions and clock "
                    + "management are not implemented in this service — they are the responsibility of the future "
                    + "Match Service.")
    @ApiResponse(responseCode = "200", description = "Match activated or inactivated",
            content = @Content(schema = @Schema(implementation = MatchActivationResponse.class)))
    @PatchMapping("/{matchId}/activation")
    public ResponseEntity<MatchActivationResponse> activation(
            @Parameter(description = "Match ID", example = "m01") @PathVariable String matchId,
            @Valid @RequestBody MatchActivationRequest request) {
        Match match = inactivateMatchUseCase.execute(new InactivateMatchCommand(matchId, request.action()));

        String message = match.isActive()
                ? "The match was successfully reactivated"
                : "The match was successfully inactivated";

        return ResponseEntity.ok(new MatchActivationResponse(match.getMatchId(), match.isActive(), message));
    }
}
