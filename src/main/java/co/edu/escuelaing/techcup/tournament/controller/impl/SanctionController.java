package co.edu.escuelaing.techcup.tournament.controller.impl;

import co.edu.escuelaing.techcup.tournament.dto.request.ApplySanctionRequest;
import co.edu.escuelaing.techcup.tournament.dto.response.SanctionResponse;
import co.edu.escuelaing.techcup.tournament.mapper.SanctionRestMapper;
import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.service.ports.ApplySanctionUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ApplySanctionUseCase.ApplySanctionCommand;
import co.edu.escuelaing.techcup.tournament.service.ports.RecordMatchFinishedForSanctionsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewPlayerSanctionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sanctions")
@Tag(name = "Sanctions", description = "Applying and querying player sanctions")
public class SanctionController {

    private final ApplySanctionUseCase applySanctionUseCase;
    private final ViewPlayerSanctionUseCase viewPlayerSanctionUseCase;
    private final RecordMatchFinishedForSanctionsUseCase recordMatchFinishedUseCase;
    private final SanctionRestMapper mapper;

    public SanctionController(ApplySanctionUseCase applySanctionUseCase,
                               ViewPlayerSanctionUseCase viewPlayerSanctionUseCase,
                               RecordMatchFinishedForSanctionsUseCase recordMatchFinishedUseCase,
                               SanctionRestMapper mapper) {
        this.applySanctionUseCase = applySanctionUseCase;
        this.viewPlayerSanctionUseCase = viewPlayerSanctionUseCase;
        this.recordMatchFinishedUseCase = recordMatchFinishedUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "Apply sanction to player",
            description = "Red card = 1 match; 2 yellow cards in different matches = 1 match; 2 yellow cards in the "
                    + "same match = red card; for conduct sanctions, the Organizer defines the number of matches suspended.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sanction applied",
                    content = @Content(schema = @Schema(implementation = SanctionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<SanctionResponse> apply(@Valid @RequestBody ApplySanctionRequest request) {
        PlayerSanction sanction = applySanctionUseCase.apply(new ApplySanctionCommand(
                request.playerId(), request.type(), request.matchesSuspended()));

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(sanction));
    }

    @Operation(summary = "Get a player's active sanctions",
            description = "Returns only sanctions still in effect (matchesRemaining > 0); served ones are omitted.")
    @ApiResponse(responseCode = "200", description = "List of the player's active sanctions",
            content = @Content(schema = @Schema(implementation = SanctionResponse.class)))
    @GetMapping("/{playerId}")
    public ResponseEntity<List<SanctionResponse>> getActiveSanctions(
            @Parameter(description = "Player ID", example = "player_123") @PathVariable String playerId) {
        List<SanctionResponse> result = viewPlayerSanctionUseCase.getActiveSanctions(playerId)
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

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
    @PostMapping("/match-finished")
    public ResponseEntity<Void> recordMatchFinished() {
        recordMatchFinishedUseCase.recordMatchFinished();
        return ResponseEntity.ok().build();
    }
}
