package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

public record RulebookErrorResponse(
        String error,
        String message,
        String hint
) {}
