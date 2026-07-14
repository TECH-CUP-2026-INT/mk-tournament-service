package co.edu.escuelaing.techcup.tournament.controller.impl;

import co.edu.escuelaing.techcup.tournament.dto.request.ApplySanctionRequest;
import co.edu.escuelaing.techcup.tournament.dto.response.SanctionResponse;
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
@Tag(name = "Sanciones", description = "Aplicación y consulta de sanciones a jugadores")
public class SanctionController {

    private final ApplySanctionUseCase applySanctionUseCase;
    private final ViewPlayerSanctionUseCase viewPlayerSanctionUseCase;
    private final RecordMatchFinishedForSanctionsUseCase recordMatchFinishedUseCase;

    public SanctionController(ApplySanctionUseCase applySanctionUseCase,
                               ViewPlayerSanctionUseCase viewPlayerSanctionUseCase,
                               RecordMatchFinishedForSanctionsUseCase recordMatchFinishedUseCase) {
        this.applySanctionUseCase = applySanctionUseCase;
        this.viewPlayerSanctionUseCase = viewPlayerSanctionUseCase;
        this.recordMatchFinishedUseCase = recordMatchFinishedUseCase;
    }

    @Operation(summary = "Aplicar sanción a jugador",
            description = "Roja = 1 partido; 2 amarillas en partidos distintos = 1 partido; 2 amarillas en el mismo "
                    + "partido = roja; por conducta el Organizador define los partidos de sanción.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sanción aplicada",
                    content = @Content(schema = @Schema(implementation = SanctionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<SanctionResponse> apply(@Valid @RequestBody ApplySanctionRequest request) {
        PlayerSanction sanction = applySanctionUseCase.apply(new ApplySanctionCommand(
                request.playerId(), request.type(), request.matchesSuspended()));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(sanction));
    }

    @Operation(summary = "Consultar sanciones activas de un jugador")
    @ApiResponse(responseCode = "200", description = "Lista de sanciones activas del jugador",
            content = @Content(schema = @Schema(implementation = SanctionResponse.class)))
    @GetMapping("/{playerId}")
    public ResponseEntity<List<SanctionResponse>> getActiveSanctions(
            @Parameter(description = "ID del jugador", example = "player_123") @PathVariable String playerId) {
        List<SanctionResponse> result = viewPlayerSanctionUseCase.getActiveSanctions(playerId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    /**
     * Punto de integración pendiente: la futura historia "Finalizar partido"
     * debe invocar este endpoint cuando un partido finaliza. Hoy no hay
     * nada que lo dispare automáticamente.
     */
    @Operation(summary = "Registrar fin de partido (reduce partidos pendientes de sanción) — integración interna",
            description = "Integración interna pendiente: hoy no existe ningún disparador automático. La futura "
                    + "historia \"Finalizar partido\" del Servicio de Partidos debe invocar este endpoint cuando un "
                    + "partido finaliza, para descontar un partido de suspensión a los jugadores sancionados.")
    @ApiResponse(responseCode = "200", description = "Fin de partido registrado")
    @PostMapping("/match-finished")
    public ResponseEntity<Void> recordMatchFinished() {
        recordMatchFinishedUseCase.recordMatchFinished();
        return ResponseEntity.ok().build();
    }

    private SanctionResponse toResponse(PlayerSanction sanction) {
        return new SanctionResponse(
                sanction.getId(), sanction.getPlayerId(), sanction.getType(),
                sanction.getMatchesRemaining(), sanction.isActive()
        );
    }
}
