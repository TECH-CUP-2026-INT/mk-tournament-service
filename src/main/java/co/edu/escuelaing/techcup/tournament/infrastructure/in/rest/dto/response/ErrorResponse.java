package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Standard error shape returned by every endpoint of this service.")
public record ErrorResponse(
        @Schema(description = "Human-readable summary of the error.", example = "El nombre del torneo es obligatorio")
        String message,
        @Schema(description = "Additional details, one entry per offending field or extra context. Empty when not applicable.")
        List<String> details
) {
    public ErrorResponse(String message) {
        this(message, List.of());
    }
}
