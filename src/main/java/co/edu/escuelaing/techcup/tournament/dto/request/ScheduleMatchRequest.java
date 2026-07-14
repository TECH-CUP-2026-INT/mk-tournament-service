package co.edu.escuelaing.techcup.tournament.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = """
        Request to schedule a match: assigns a court, a referee, and a date/time to an already \
        generated matchup. Fails with 409 if the court or referee already has another match at the \
        same date and time.""")
public record ScheduleMatchRequest(

        @Schema(description = "ID of the matchup (fixture entry) being scheduled.", example = "m01")
        @NotBlank(message = "El id de la matchup pairing es obligatorio")
        String matchupId,

        @Schema(description = "Match date.", example = "2026-08-05")
        @NotNull(message = "La fecha del partido es obligatoria")
        LocalDate matchDate,

        @Schema(description = "Match time.", example = "09:00:00")
        @NotNull(message = "La hora del partido es obligatoria")
        LocalTime matchTime,

        @Schema(description = "ID of the assigned court.", example = "court-1")
        @NotBlank(message = "El id de la cancha es obligatorio")
        String courtId,

        @Schema(description = "ID of the assigned referee.", example = "ref-1")
        @NotBlank(message = "El id del árbitro es obligatorio")
        String refereeId
) {}
