package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = """
        One court on the interactive campus map, with its assigned match (if any), \
        schedule and status. `status` and `statusLabel` are always present together — \
        the frontend must never rely on color alone to convey status (WCAG 2.1 AA).""")
public record CourtMapEntryResponse(
        @Schema(description = "Court ID.", example = "court-1") String courtId,
        @Schema(description = "Campus map section.", example = "CANCHA_1") CourtSection section,
        @Schema(description = "Court description.", example = "Synthetic grass court, north side of campus") String description,
        @Schema(description = "Court image ID, or null if none was uploaded.", example = "img-1") String imageId,
        @Schema(description = "Machine-readable status: AVAILABLE, SCHEDULED, IN_PROGRESS or FINISHED.", example = "SCHEDULED")
        CourtMapStatus status,
        @Schema(description = "Human-readable label for the status, for accessible rendering alongside color/icon.",
                example = "Scheduled")
        String statusLabel,
        @Schema(description = "ID of the match assigned to this court, or null if the court is available.", example = "m01")
        String matchId,
        @Schema(description = "ID of the home team, or null if not applicable.", example = "team_xyz789") String homeTeamId,
        @Schema(description = "ID of the away team, or null if not applicable.", example = "team_abc456") String awayTeamId,
        @Schema(description = "Scheduled match date, or null if not scheduled yet.", example = "2026-08-05") LocalDate matchDate,
        @Schema(description = "Scheduled match time, or null if not scheduled yet.", example = "09:00:00") LocalTime matchTime
) {
    public enum CourtMapStatus { AVAILABLE, SCHEDULED, IN_PROGRESS, FINISHED }
}
