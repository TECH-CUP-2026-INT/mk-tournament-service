package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateTournamentRequest(

        @Schema(description = "Nombre del torneo", example = "TechCup Fútbol 2026", maxLength = 100)
        @NotBlank(message = "El nombre del torneo es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String name,

        @Schema(description = "Tipo de torneo", example = "NORMAL")
        @NotNull(message = "El tipo de torneo es obligatorio")
        TournamentType type,

        @Schema(description = "Formato del torneo", example = "BRACKETS")
        @NotNull(message = "El formato del torneo es obligatorio")
        TournamentFormat format,

        @Schema(description = "Número de equipos participantes (mínimo 3)", example = "8")
        @Min(value = 3, message = "La cantidad de equipos debe ser mayor o igual a 3")
        int numberOfTeams,

        @Schema(description = "Costo de inscripción en pesos colombianos", example = "50000.00")
        @NotNull(message = "El costo de inscripción es obligatorio")
        @DecimalMin(value = "0", message = "El costo de inscripción no puede ser negativo")
        BigDecimal cost,

        @Schema(description = "Fecha de inicio del torneo", example = "2026-08-01")
        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate startDate,

        @Schema(description = "Fecha de finalización (se auto-deriva de startDate si el torneo es LIGHTNING)", example = "2026-08-31")
        LocalDate endDate,

        @Schema(description = "Fecha límite de inscripción de equipos", example = "2026-07-25")
        @NotNull(message = "La fecha de cierre de inscripciones es obligatoria")
        LocalDate registrationDeadline,

        @Schema(description = "Hora de inicio de los partidos (solo aplica a torneos LIGHTNING)", example = "09:00")
        LocalTime matchStartTime,

        @Schema(description = "Hora de finalización de los partidos (solo aplica a torneos LIGHTNING)", example = "21:00")
        LocalTime matchEndTime
) {}