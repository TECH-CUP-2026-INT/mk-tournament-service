package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A court registered for a tournament.")
public record CourtResponse(
        @Schema(description = "Court ID.", example = "court-1") String courtId,
        @Schema(description = "Tournament ID.", example = "abc123") String tournamentId,
        @Schema(description = "Campus map section the court is in.", example = "CANCHA_1") String section,
        @Schema(description = "Court description.", example = "Synthetic grass court, north side of campus") String description,
        @Schema(description = "ID of the court's image, or null if none was uploaded.") String imageId,
        @Schema(description = "Human-readable confirmation message.", example = "Court registered successfully")
        String message
) {}
