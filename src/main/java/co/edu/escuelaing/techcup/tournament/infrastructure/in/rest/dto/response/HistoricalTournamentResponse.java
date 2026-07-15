package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Read-only summary of a finished tournament kept in the historical archive.")
public record HistoricalTournamentResponse(
        @Schema(description = "Tournament ID.", example = "abc123") String id,
        @Schema(description = "Tournament name.", example = "TechCup Football 2026") String name,
        @Schema(description = "Number of participating teams.", example = "8") int numberOfTeams,
        @Schema(description = "Enrollment fee, in Colombian pesos (COP).", example = "50000.00") BigDecimal cost,
        @Schema(description = "Tournament start date.", example = "2026-08-01") LocalDate startDate,
        @Schema(description = "Tournament end date.", example = "2026-08-31") LocalDate endDate,
        @Schema(description = "Enrollment deadline.", example = "2026-07-25") LocalDate registrationDeadline,
        @Schema(description = "Final lifecycle status (always FINISHED).", example = "FINISHED") TournamentStatus status,
        @Schema(description = "ID of the champion team, or null if none was assigned.", example = "team_xyz789")
        String championTeamId
) {}
