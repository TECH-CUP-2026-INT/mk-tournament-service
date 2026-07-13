package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentResponse(
        String enrollmentId,
        EnrollmentStatus status,
        LocalDateTime reservationExpiresAt
) {}
