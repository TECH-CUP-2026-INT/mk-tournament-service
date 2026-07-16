package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "A single audit log entry, captured automatically for every successful application action.")
public record AuditEventResponse(
        @Schema(description = "When the action was performed (UTC).") Instant timestamp,
        @Schema(description = "Who performed the action. Currently always \"system\" — real user attribution is pending the future Identity Service.",
                example = "system")
        String actor,
        @Schema(description = "Action performed, as \"<ServiceClass>.<method>\".", example = "CreateTournamentService.create")
        String actionType,
        @Schema(description = "Best-effort ID of the entity affected by the action, or null if it couldn't be determined.",
                example = "abc123")
        String affectedEntityId
) {}
