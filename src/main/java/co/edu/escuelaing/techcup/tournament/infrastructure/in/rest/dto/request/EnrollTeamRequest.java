package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request for a team captain to enroll their team in a tournament.")
public record EnrollTeamRequest(
        @Schema(description = "ID of the team to enroll. The tournament must be ACTIVE and have open slots.",
                example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "El id del equipo es obligatorio") UUID teamId
) {}
