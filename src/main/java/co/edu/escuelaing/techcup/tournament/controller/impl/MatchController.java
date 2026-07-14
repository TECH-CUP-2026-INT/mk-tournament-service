package co.edu.escuelaing.techcup.tournament.controller.impl;

import co.edu.escuelaing.techcup.tournament.dto.request.MatchActivationRequest;
import co.edu.escuelaing.techcup.tournament.dto.request.ScheduleMatchRequest;
import co.edu.escuelaing.techcup.tournament.dto.response.MatchActivationResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.ScheduledMatchResponse;
import co.edu.escuelaing.techcup.tournament.service.Match;
import co.edu.escuelaing.techcup.tournament.service.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateMatchUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateMatchUseCase.InactivateMatchCommand;
import co.edu.escuelaing.techcup.tournament.service.ports.ScheduleMatchUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ScheduleMatchUseCase.ScheduleMatchCommand;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matches")
@Tag(name = "Partidos", description = "Programación y activación/inactivación de partidos")
public class MatchController {

    private final ScheduleMatchUseCase scheduleMatchUseCase;
    private final InactivateMatchUseCase inactivateMatchUseCase;

    public MatchController(ScheduleMatchUseCase scheduleMatchUseCase,
                            InactivateMatchUseCase inactivateMatchUseCase) {
        this.scheduleMatchUseCase = scheduleMatchUseCase;
        this.inactivateMatchUseCase = inactivateMatchUseCase;
    }

    @Operation(summary = "Programar partido (asignar fecha, hora, cancha y árbitro)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Partido programado",
                    content = @Content(schema = @Schema(implementation = ScheduledMatchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto de horario con la cancha o el árbitro", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ScheduledMatchResponse> schedule(@Valid @RequestBody ScheduleMatchRequest request) {
        ScheduledMatch scheduled = scheduleMatchUseCase.schedule(new ScheduleMatchCommand(
                request.matchupId(), request.matchDate(), request.matchTime(),
                request.courtId(), request.refereeId()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(new ScheduledMatchResponse(
                scheduled.getId(), scheduled.getMatchupId(), scheduled.getCourtId(),
                scheduled.getRefereeId(), scheduled.getMatchDate(), scheduled.getMatchTime()
        ));
    }

    @Operation(summary = "Activar o inactivar partido",
            description = "Un partido inactivo conserva los datos ya registrados pero bloquea el registro de marcador, "
                    + "ganador de penales y no-show. Tarjetas, sustituciones y reloj no se implementan en este servicio: "
                    + "son responsabilidad del futuro Servicio de Partidos.")
    @ApiResponse(responseCode = "200", description = "Partido activado o inactivado",
            content = @Content(schema = @Schema(implementation = MatchActivationResponse.class)))
    @PatchMapping("/{matchId}/activation")
    public ResponseEntity<MatchActivationResponse> activation(
            @Parameter(description = "ID del partido", example = "m01") @PathVariable String matchId,
            @Valid @RequestBody MatchActivationRequest request) {
        Match match = inactivateMatchUseCase.execute(new InactivateMatchCommand(matchId, request.action()));

        String message = match.isActive()
                ? "El partido fue reactivado correctamente"
                : "El partido fue inactivado correctamente";

        return ResponseEntity.ok(new MatchActivationResponse(match.getMatchId(), match.isActive(), message));
    }
}
