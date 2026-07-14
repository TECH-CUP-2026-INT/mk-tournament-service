package co.edu.escuelaing.techcup.tournament.dto.response;

import java.time.LocalDateTime;

public record EnrolledTeamResponse(
        String teamId,
        String teamName,
        String logoUrl,
        String enrollmentId,
        LocalDateTime confirmationDate
) {}
