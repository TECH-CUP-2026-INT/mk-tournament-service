package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Confirmation that a tournament was permanently deleted.")
public record DeleteTournamentResponse(
        @Schema(description = "Human-readable confirmation message.", example = "Tournament 'abc123' has been permanently deleted.")
        String message
) {
}
