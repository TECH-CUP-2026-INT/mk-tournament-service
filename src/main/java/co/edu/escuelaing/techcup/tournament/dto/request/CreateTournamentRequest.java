package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateTournamentRequest(

        @NotBlank(message = "El nombre del torneo es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String name,

        @NotNull(message = "El tipo de torneo es obligatorio")
        TournamentType type,

        @NotNull(message = "El formato del torneo es obligatorio")
        TournamentFormat format,

        @Min(value = 3, message = "La cantidad de equipos debe ser mayor o igual a 3")
        int numberOfTeams,

        @NotNull(message = "El costo de inscripción es obligatorio")
        @DecimalMin(value = "0", message = "El costo de inscripción no puede ser negativo")
        BigDecimal cost,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate startDate,

        LocalDate endDate,

        @NotNull(message = "La fecha de cierre de inscripciones es obligatoria")
        LocalDate registrationDeadline,

        LocalTime matchStartTime,

        LocalTime matchEndTime
) {}