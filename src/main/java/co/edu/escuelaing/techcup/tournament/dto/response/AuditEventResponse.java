package co.edu.escuelaing.techcup.tournament.dto.response;

import java.time.Instant;

public record AuditEventResponse(
        Instant timestamp,
        String actor,
        String actionType,
        String affectedEntityId
) {}
