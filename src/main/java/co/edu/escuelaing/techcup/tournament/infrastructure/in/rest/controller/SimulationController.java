package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetTournamentByMatchUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ProcessMatchResultUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ProcessMatchResultUseCase.ProcessMatchResultCommand;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.SimulateMatchResultRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Modo simulación: permite disparar {@link ProcessMatchResultUseCase} por el
 * mismo camino que el listener de RabbitMQ, sin depender de un broker ni del
 * Servicio de Matches. Solo activo con {@code spring.profiles.active=dev}.
 */
@Tag(name = "Simulation", description = "Dev-only: simulate a match result without RabbitMQ/Matches")
@RestController
@RequestMapping("/sim")
@Profile("dev")
@RequiredArgsConstructor
public class SimulationController {

    private final ProcessMatchResultUseCase processMatchResult;
    private final GetTournamentByMatchUseCase getTournamentByMatch;

    @Operation(summary = "Simulate a match result",
            description = "Resolves the tournament by matchId and calls ProcessMatchResult exactly like the "
                    + "techcup.match.finished listener would. Dev profile only.")
    @PostMapping("/partidos/{matchId}/resultado")
    public ResponseEntity<Void> simulateResult(
            @PathVariable UUID matchId,
            @Valid @RequestBody SimulateMatchResultRequest request) {
        Tournament tournament = getTournamentByMatch.getByMatch(matchId);

        processMatchResult.process(new ProcessMatchResultCommand(
                matchId,
                tournament.getId(),
                request.fase(),
                request.golesA(),
                request.golesB(),
                request.ganadorId(),
                request.ausenteId()));

        return ResponseEntity.ok().build();
    }
}
