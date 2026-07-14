package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.EnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Result of enrolling a team in a tournament.")
public record EnrollmentResponse(
        @Schema(description = "Unique enrollment ID.", example = "enr_abc123")
        String enrollmentId,
        @Schema(description = "Enrollment status: RESERVED, PENDING_PAYMENT, ENROLLED, REJECTED or EXPIRED.",
                example = "RESERVED")
        EnrollmentStatus status,
        @Schema(description = "Date/time the team's slot reservation expires, if it hasn't been confirmed yet (null once ENROLLED).")
        LocalDateTime reservationExpiresAt
) {}
