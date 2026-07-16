package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "A court registered for a tournament.")
public record CourtResponse(
        @Schema(description = "Court ID.", example = "6f9619ff-8b86-d011-b42d-00cf4fc964ff") UUID courtId,
        @Schema(description = "Tournament ID.", example = "550e8400-e29b-41d4-a716-446655440000") UUID tournamentId,
        @Schema(description = "Campus map section the court is in.", example = "CANCHA_1") String section,
        @Schema(description = "Court description.", example = "Synthetic grass court, north side of campus") String description,
        @Schema(description = "ID of the court's image, or null if none was uploaded.") String imageId,
        @Schema(description = "Human-readable confirmation message.", example = "Court registered successfully")
        String message
) {}
