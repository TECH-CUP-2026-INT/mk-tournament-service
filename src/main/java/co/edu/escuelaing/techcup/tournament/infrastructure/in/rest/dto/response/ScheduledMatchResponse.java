package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "A match scheduled with a matchup, court, referee, date and time.")
public record ScheduledMatchResponse(
        @Schema(description = "Scheduled match ID.", example = "sm1") String id,
        @Schema(description = "ID of the matchup this schedule is for.", example = "m01") String matchupId,
        @Schema(description = "Assigned court ID.", example = "court-1") String courtId,
        @Schema(description = "Assigned referee ID.", example = "ref-1") String refereeId,
        @Schema(description = "Match date.", example = "2026-08-05") LocalDate matchDate,
        @Schema(description = "Match time.", example = "09:00:00") LocalTime matchTime
) {}
