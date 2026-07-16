package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Schema(description = "A match scheduled with a matchup, court, referee, date and time.")
public record ScheduledMatchResponse(
        @Schema(description = "Scheduled match ID.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID id,
        @Schema(description = "ID of the matchup this schedule is for.", example = "550e8400-e29b-41d4-a716-446655440000") UUID matchupId,
        @Schema(description = "Assigned court ID.", example = "6f9619ff-8b86-d011-b42d-00cf4fc964ff") UUID courtId,
        @Schema(description = "Assigned referee ID.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID refereeId,
        @Schema(description = "Match date.", example = "2026-08-05") LocalDate matchDate,
        @Schema(description = "Match time.", example = "09:00:00") LocalTime matchTime
) {}
