package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Full current state of a tournament.")
public record TournamentResponse(
        @Schema(description = "Unique tournament ID.", example = "abc123") String id,
        @Schema(description = "Tournament name.", example = "TechCup Football 2026") String name,
        @Schema(description = "Tournament type.", example = "NORMAL") TournamentType type,
        @Schema(description = "Bracket format.", example = "BRACKETS") TournamentFormat format,
        @Schema(description = "Number of participating teams.", example = "8") int numberOfTeams,
        @Schema(description = "Enrollment fee, in Colombian pesos (COP).", example = "50000.00") BigDecimal cost,
        @Schema(description = "Tournament start date.", example = "2026-08-01") LocalDate startDate,
        @Schema(description = "Tournament end date.", example = "2026-08-31") LocalDate endDate,
        @Schema(description = "Enrollment deadline.", example = "2026-07-25") LocalDate registrationDeadline,
        @Schema(description = "Match start time (LIGHTNING tournaments only).") LocalTime matchStartTime,
        @Schema(description = "Match end time (LIGHTNING tournaments only).") LocalTime matchEndTime,
        @Schema(description = "Current lifecycle status.", example = "ACTIVE") TournamentStatus status,
        @Schema(description = "Whether the tournament is currently paused.") boolean paused,
        @Schema(description = "Whether the tournament is currently active (not inactivated).") boolean active
) {}
