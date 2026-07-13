package co.edu.escuelaing.techcup.tournament.controller.impl;

import co.edu.escuelaing.techcup.tournament.dto.request.ApplySanctionRequest;
import co.edu.escuelaing.techcup.tournament.dto.response.SanctionResponse;
import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.service.ports.ApplySanctionUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ApplySanctionUseCase.ApplySanctionCommand;
import co.edu.escuelaing.techcup.tournament.service.ports.RecordMatchFinishedForSanctionsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewPlayerSanctionUseCase;
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

    @PostMapping
    public ResponseEntity<SanctionResponse> apply(@Valid @RequestBody ApplySanctionRequest request) {
        PlayerSanction sanction = applySanctionUseCase.apply(new ApplySanctionCommand(
                request.playerId(), request.type(), request.matchesSuspended()));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(sanction));
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<List<SanctionResponse>> getActiveSanctions(@PathVariable String playerId) {
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
