package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Pointer to the tournament currently IN_PROGRESS. Contract consumed by Estadísticas' \
        TournamentClientImpl: the field is named exactly "id" and is a plain UUID string \
        (Estadísticas does UUID.fromString on it) — do not change the shape.""")
public record ActiveTournamentResponse(
        @Schema(description = "ID of the tournament currently in progress, as a UUID string.",
                example = "550e8400-e29b-41d4-a716-446655440000")
        String id
) {}
