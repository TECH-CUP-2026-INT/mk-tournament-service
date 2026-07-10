// infrastructure/rest/dto/CreateTournamentRequest.java
package co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTournamentRequest(

        @NotBlank(message = "El nombre del torneo es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String name,

        @Min(value = 2, message = "La cantidad de equipos debe ser mayor o igual a 2")
        int numberOfTeams,

        @NotNull(message = "El costo de inscripción es obligatorio")
        @DecimalMin(value = "0", message = "El costo de inscripción no puede ser negativo")
        BigDecimal cost,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate startDate,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate endDate,

        @NotNull(message = "La fecha de cierre de inscripciones es obligatoria")
        LocalDate registrationDeadline
) {}