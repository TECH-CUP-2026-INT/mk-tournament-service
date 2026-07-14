package co.edu.escuelaing.techcup.tournament.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleMatchRequest(

        @Schema(description = "ID del emparejamiento (matchup) a programar", example = "m01")
        @NotBlank(message = "El id de la matchup pairing es obligatorio")
        String matchupId,

        @Schema(description = "Fecha del partido", example = "2026-08-05")
        @NotNull(message = "La fecha del partido es obligatoria")
        LocalDate matchDate,

        @Schema(description = "Hora del partido", example = "09:00:00")
        @NotNull(message = "La hora del partido es obligatoria")
        LocalTime matchTime,

        @Schema(description = "ID de la cancha asignada", example = "court-1")
        @NotBlank(message = "El id de la cancha es obligatorio")
        String courtId,

        @Schema(description = "ID del árbitro asignado", example = "ref-1")
        @NotBlank(message = "El id del árbitro es obligatorio")
        String refereeId
) {}
