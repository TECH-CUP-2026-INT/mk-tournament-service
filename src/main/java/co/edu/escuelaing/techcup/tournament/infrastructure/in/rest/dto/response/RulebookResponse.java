package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Confirmation that a rulebook PDF was attached to a tournament.")
public record RulebookResponse(
        @Schema(description = "Tournament ID.", example = "abc123") String tournamentId,
        @Schema(description = "Internal ID of the stored rulebook file.", example = "65f1a2b3c4d5e6f7a8b9c0d1") String rulebookFileId,
        @Schema(description = "Human-readable confirmation message.", example = "Rulebook attached successfully")
        String message
) {}
