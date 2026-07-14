package co.edu.escuelaing.techcup.tournament.dto.response;

public record RulebookErrorResponse(
        String error,
        String message,
        String hint
) {}
