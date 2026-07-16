package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the ID of the currently active tournament")
public record ActiveTournamentResponse(
        @Schema(description = "Active tournament ID", example = "550e8400-e29b-41d4-a716-446655440000")
        String id
) {}
