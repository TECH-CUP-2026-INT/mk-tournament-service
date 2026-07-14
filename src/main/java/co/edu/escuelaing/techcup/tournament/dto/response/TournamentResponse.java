package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Respuesta con el estado completo de un torneo")
public record TournamentResponse(
        @Schema(description = "ID único del torneo", example = "abc123") String id,
        @Schema(description = "Nombre del torneo", example = "TechCup Fútbol 2026") String name,
        @Schema(description = "Tipo de torneo", example = "NORMAL") TournamentType type,
        @Schema(description = "Formato del torneo", example = "BRACKETS") TournamentFormat format,
        @Schema(description = "Número de equipos participantes", example = "8") int numberOfTeams,
        @Schema(description = "Costo de inscripción en pesos colombianos", example = "50000.00") BigDecimal cost,
        @Schema(description = "Fecha de inicio del torneo", example = "2026-08-01") LocalDate startDate,
        @Schema(description = "Fecha de finalización del torneo", example = "2026-08-31") LocalDate endDate,
        @Schema(description = "Fecha límite de inscripción de equipos", example = "2026-07-25") LocalDate registrationDeadline,
        @Schema(description = "Hora de inicio de los partidos") LocalTime matchStartTime,
        @Schema(description = "Hora de finalización de los partidos") LocalTime matchEndTime,
        @Schema(description = "Estado del torneo", example = "ACTIVE") TournamentStatus status,
        @Schema(description = "Indica si el torneo está pausado") boolean paused,
        @Schema(description = "Indica si el torneo está activo") boolean active
) {}
