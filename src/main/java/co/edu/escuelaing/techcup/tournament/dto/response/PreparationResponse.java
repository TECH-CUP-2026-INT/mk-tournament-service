package co.edu.escuelaing.techcup.tournament.dto.response;

import java.util.List;

public record PreparationResponse(
        String status,
        boolean readyToActivate,
        long approvedTeamsCount,
        List<String> missingRequirements
) {}
