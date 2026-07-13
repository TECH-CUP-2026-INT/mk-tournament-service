package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.SanctionType;

public record SanctionResponse(
        String id,
        String playerId,
        SanctionType type,
        int matchesRemaining,
        boolean active
) {}
