package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.ParticipantStatus;

public record InactivateUserResponse(
        String tournamentId,
        String userId,
        ParticipantStatus status,
        String message
) {}
