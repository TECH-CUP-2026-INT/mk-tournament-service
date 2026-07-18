package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Result of inactivating or reactivating a tournament.")
public record InactivateTournamentResponse(
        @Schema(description = "Tournament ID.", example = "550e8400-e29b-41d4-a716-446655440000") UUID tournamentId,
        @Schema(description = "Tournament lifecycle status.", example = "ACTIVE") TournamentStatus status,
        @Schema(description = "Whether the tournament is now active.") boolean active,
        @Schema(description = "Human-readable confirmation message.", example = "The tournament was successfully inactivated")
        String message
) {}
