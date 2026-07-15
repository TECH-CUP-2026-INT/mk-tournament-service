package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of inactivating or reactivating a tournament.")
public record InactivateTournamentResponse(
        @Schema(description = "Tournament ID.", example = "abc123") String tournamentId,
        @Schema(description = "Tournament lifecycle status.", example = "ACTIVE") TournamentStatus status,
        @Schema(description = "Whether the tournament is now active.") boolean active,
        @Schema(description = "Human-readable confirmation message.", example = "The tournament was successfully inactivated")
        String message
) {}
