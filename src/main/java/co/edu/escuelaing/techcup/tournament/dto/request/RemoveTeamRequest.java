package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.RemovalReason;
import jakarta.validation.constraints.NotNull;

public record RemoveTeamRequest(
        @NotNull(message = "El motivo de remoción es obligatorio") RemovalReason reason
) {}
