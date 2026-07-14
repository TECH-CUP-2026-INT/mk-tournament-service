package co.edu.escuelaing.techcup.tournament.controller.impl;

import co.edu.escuelaing.techcup.tournament.dto.response.AuditEventResponse;
import co.edu.escuelaing.techcup.tournament.service.AuditEventFilter;
import co.edu.escuelaing.techcup.tournament.service.ports.ConsultAuditEventsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * TC-40: audit log query. Currently without real access control (see
 * SecurityConfig) — pending the future Identity Service (JWT + Admin/Organizer roles).
 */
@RestController
@RequestMapping("/audit-events")
@Tag(name = "Audit", description = "Querying the tournament service's audit log (TC-40)")
public class AuditEventController {

    private final ConsultAuditEventsUseCase consultAuditEventsUseCase;

    public AuditEventController(ConsultAuditEventsUseCase consultAuditEventsUseCase) {
        this.consultAuditEventsUseCase = consultAuditEventsUseCase;
    }

    @Operation(summary = "Query audit events with optional filters (date, event type, tournament)",
            description = "Every action performed through the application services is captured automatically via an "
                    + "AOP aspect. All filters are optional and combined with AND when several are present.")
    @ApiResponse(responseCode = "200", description = "List of audit events matching the filters",
            content = @Content(schema = @Schema(implementation = AuditEventResponse.class)))
    @GetMapping
    public ResponseEntity<List<AuditEventResponse>> consult(
            @Parameter(description = "Start of the date range (ISO 8601)", example = "2026-01-01", required = false)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "End of the date range (ISO 8601)", example = "2026-01-31", required = false)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "Type of audited action, as \"<ServiceClass>.<method>\"",
                    example = "CreateTournamentService.create", required = false)
            @RequestParam(required = false) String eventType,
            @Parameter(description = "ID of the tournament to filter by", example = "abc123", required = false)
            @RequestParam(required = false) String tournamentId) {

        List<AuditEventResponse> result = consultAuditEventsUseCase
                .consult(new AuditEventFilter(from, to, eventType, tournamentId))
                .stream()
                .map(e -> new AuditEventResponse(e.getTimestamp(), e.getActor(), e.getActionType(), e.getAffectedEntityId()))
                .toList();

        return ResponseEntity.ok(result);
    }
}
