package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Schema(description = """
        Request to schedule a match: assigns a court, a referee, and a date/time to an already \
        generated matchup. Fails with 409 if the court or referee already has another match at the \
        same date and time.""")
public record ScheduleMatchRequest(

        @Schema(description = "ID of the matchup (fixture entry) being scheduled.", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "El id de la matchup pairing es obligatorio")
        UUID matchupId,

        @Schema(description = "Match date.", example = "2026-08-05")
        @NotNull(message = "La fecha del partido es obligatoria")
        LocalDate matchDate,

        @Schema(description = "Match time.", example = "09:00:00")
        @NotNull(message = "La hora del partido es obligatoria")
        LocalTime matchTime,

        @Schema(description = "ID of the assigned court.", example = "6f9619ff-8b86-d011-b42d-00cf4fc964ff")
        @NotNull(message = "El id de la cancha es obligatorio")
        UUID courtId,

        @Schema(description = "ID of the assigned referee.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull(message = "El id del árbitro es obligatorio")
        UUID refereeId
) {}
