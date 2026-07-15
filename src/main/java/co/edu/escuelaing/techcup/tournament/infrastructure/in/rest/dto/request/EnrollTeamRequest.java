package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request for a team captain to enroll their team in a tournament.")
public record EnrollTeamRequest(
        @Schema(description = "ID of the team to enroll. The tournament must be ACTIVE and have open slots.",
                example = "team_xyz789")
        @NotBlank(message = "El id del equipo es obligatorio") String teamId
) {}
