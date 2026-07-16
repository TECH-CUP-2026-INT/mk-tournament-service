package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.EnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Result of enrolling a team in a tournament.")
public record EnrollmentResponse(
        @Schema(description = "Unique enrollment ID.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID enrollmentId,
        @Schema(description = "Enrollment status: RESERVED, PENDING_PAYMENT, ENROLLED, REJECTED or EXPIRED.",
                example = "RESERVED")
        EnrollmentStatus status,
        @Schema(description = "Date/time the team's slot reservation expires, if it hasn't been confirmed yet (null once ENROLLED).")
        LocalDateTime reservationExpiresAt
) {}
