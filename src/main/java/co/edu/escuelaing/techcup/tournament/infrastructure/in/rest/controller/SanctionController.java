package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.ApplySanctionRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.SanctionResponse;
import co.edu.escuelaing.techcup.tournament.application.mapper.SanctionRestMapper;
import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ApplySanctionUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ApplySanctionUseCase.ApplySanctionCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RecordMatchFinishedForSanctionsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewPlayerSanctionUseCase;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller.swagger.SanctionControllerSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sanctions")
@RequiredArgsConstructor
public class SanctionController implements SanctionControllerSwagger {

    private final ApplySanctionUseCase applySanctionUseCase;
    private final ViewPlayerSanctionUseCase viewPlayerSanctionUseCase;
    private final RecordMatchFinishedForSanctionsUseCase recordMatchFinishedUseCase;
    private final SanctionRestMapper mapper;

    @Override
    @PostMapping
    public ResponseEntity<SanctionResponse> apply(@Valid @RequestBody ApplySanctionRequest request) {
        PlayerSanction sanction = applySanctionUseCase.apply(new ApplySanctionCommand(
                request.playerId(), request.type(), request.matchesSuspended()));

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(sanction));
    }

    @Override
    @GetMapping("/{playerId}")
    public ResponseEntity<List<SanctionResponse>> getActiveSanctions(@PathVariable UUID playerId) {
        List<SanctionResponse> result = viewPlayerSanctionUseCase.getActiveSanctions(playerId)
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    @Override
    @PostMapping("/match-finished")
    public ResponseEntity<Void> recordMatchFinished() {
        recordMatchFinishedUseCase.recordMatchFinished();
        return ResponseEntity.ok().build();
    }
}
