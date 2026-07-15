package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller.swagger;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.AuditEventResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Audit", description = "Querying the tournament service's audit log (TC-40)")
public interface AuditEventControllerSwagger {

    @Operation(summary = "Query audit events with optional filters (date, event type, tournament)",
            description = "Every action performed through the application services is captured automatically via an "
                    + "AOP aspect. All filters are optional and combined with AND when several are present.")
    @ApiResponse(responseCode = "200", description = "List of audit events matching the filters",
            content = @Content(schema = @Schema(implementation = AuditEventResponse.class)))
    ResponseEntity<List<AuditEventResponse>> consult(
            @Parameter(description = "Start of the date range (ISO 8601)", example = "2026-01-01", required = false)
            LocalDate from,
            @Parameter(description = "End of the date range (ISO 8601)", example = "2026-01-31", required = false)
            LocalDate to,
            @Parameter(description = "Type of audited action, as \"<ServiceClass>.<method>\"",
                    example = "CreateTournamentService.create", required = false)
            String eventType,
            @Parameter(description = "ID of the tournament to filter by", example = "abc123", required = false)
            String tournamentId);
}
