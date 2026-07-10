package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.EliminationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TournamentRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        @NotNull(message = "La fecha de inicio es obligatoria") LocalDate startDate,
        @NotNull(message = "La fecha de fin es obligatoria") LocalDate endDate,
        @NotNull(message = "El tipo de eliminación es obligatorio") EliminationType eliminationType
) {}
