package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.DisqualificationReason;
import jakarta.validation.constraints.NotNull;

public record DisqualifyTeamRequest(
        @NotNull(message = "El motivo de descalificación es obligatorio") DisqualificationReason reason
) {}
