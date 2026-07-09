package co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto;

import co.edu.escuelaing.techcup.tournament.domain.model.RemovalReason;
import jakarta.validation.constraints.NotNull;

public record RemoveTeamRequest(
        @NotNull(message = "El motivo de remoción es obligatorio") RemovalReason reason
) {}
