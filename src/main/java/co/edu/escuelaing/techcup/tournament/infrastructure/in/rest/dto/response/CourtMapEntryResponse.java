package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Schema(description = """
        One court on the interactive campus map, with its assigned match (if any), \
        schedule and status. `status` and `statusLabel` are always present together — \
        the frontend must never rely on color alone to convey status (WCAG 2.1 AA).""")
public record CourtMapEntryResponse(
        @Schema(description = "Court ID.", example = "6f9619ff-8b86-d011-b42d-00cf4fc964ff") UUID courtId,
        @Schema(description = "Campus map section.", example = "CANCHA_1") CourtSection section,
        @Schema(description = "Court description.", example = "Synthetic grass court, north side of campus") String description,
        @Schema(description = "Court image ID, or null if none was uploaded.", example = "65f1a2b3c4d5e6f7a8b9c0d1") String imageId,
        @Schema(description = "Machine-readable status: AVAILABLE, SCHEDULED, IN_PROGRESS or FINISHED.", example = "SCHEDULED")
        CourtMapStatus status,
        @Schema(description = "Human-readable label for the status, for accessible rendering alongside color/icon.",
                example = "Scheduled")
        String statusLabel,
        @Schema(description = "ID of the match assigned to this court, or null if the court is available.", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID matchId,
        @Schema(description = "ID of the home team, or null if not applicable.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID homeTeamId,
        @Schema(description = "ID of the away team, or null if not applicable.", example = "6ba7b810-9dad-11d1-80b4-00c04fd430c8") UUID awayTeamId,
        @Schema(description = "Scheduled match date, or null if not scheduled yet.", example = "2026-08-05") LocalDate matchDate,
        @Schema(description = "Scheduled match time, or null if not scheduled yet.", example = "09:00:00") LocalTime matchTime
) {
    public enum CourtMapStatus { AVAILABLE, SCHEDULED, IN_PROGRESS, FINISHED }
}
