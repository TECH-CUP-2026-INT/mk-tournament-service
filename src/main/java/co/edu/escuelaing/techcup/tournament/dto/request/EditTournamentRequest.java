package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record EditTournamentRequest(
        @Schema(description = "Nombre del torneo", example = "TechCup Fútbol 2026", maxLength = 100)
        @Size(max = 100) String name,

        @Schema(description = "Tipo de torneo", example = "NORMAL")
        TournamentType type,

        @Schema(description = "Formato del torneo", example = "BRACKETS")
        TournamentFormat format,

        @Schema(description = "Número de equipos participantes", example = "8")
        Integer numberOfTeams,

        @Schema(description = "Costo de inscripción en pesos colombianos", example = "50000.00")
        @DecimalMin(value = "0") BigDecimal cost,

        @Schema(description = "Fecha límite de inscripción de equipos", example = "2026-07-25")
        LocalDate registrationDeadline,

        @Schema(description = "Fecha de inicio del torneo", example = "2026-08-01")
        LocalDate startDate,

        @Schema(description = "Fecha de finalización del torneo", example = "2026-08-31")
        LocalDate endDate,

        @Schema(description = "Hora de inicio de los partidos", example = "09:00")
        LocalTime matchStartTime,

        @Schema(description = "Hora de finalización de los partidos", example = "21:00")
        LocalTime matchEndTime
) {}
