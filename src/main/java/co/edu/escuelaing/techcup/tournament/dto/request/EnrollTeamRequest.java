package co.edu.escuelaing.techcup.tournament.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EnrollTeamRequest(
        @NotBlank(message = "El id del equipo es obligatorio") String teamId
) {}
