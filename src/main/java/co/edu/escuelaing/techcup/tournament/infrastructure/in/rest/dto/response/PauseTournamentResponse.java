package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of pausing or resuming a tournament.")
public record PauseTournamentResponse(
        @Schema(description = "Tournament ID.", example = "abc123") String tournamentId,
        @Schema(description = "Tournament lifecycle status (unaffected by pausing).", example = "ACTIVE") TournamentStatus status,
        @Schema(description = "Whether the tournament is now paused.") boolean paused,
        @Schema(description = "Human-readable confirmation message.", example = "The tournament was successfully paused")
        String message
) {}
