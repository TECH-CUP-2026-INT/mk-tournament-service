package co.edu.escuelaing.techcup.tournament.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record EnrollTeamRequest(
        @Schema(description = "ID del equipo a inscribir", example = "team_xyz789")
        @NotBlank(message = "El id del equipo es obligatorio") String teamId
) {}
