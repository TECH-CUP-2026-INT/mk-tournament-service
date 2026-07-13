package co.edu.escuelaing.techcup.tournament.dto.response;

public record CourtResponse(
        String courtId,
        String tournamentId,
        String section,
        String description,
        String imageId,
        String message
) {}
