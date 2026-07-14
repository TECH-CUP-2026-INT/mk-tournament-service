package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.EnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta de inscripción de equipo en torneo")
public record EnrollmentResponse(
        @Schema(description = "ID único de la inscripción", example = "enr_abc123")
        String enrollmentId,
        @Schema(description = "Estado de la inscripción", example = "RESERVED")
        EnrollmentStatus status,
        @Schema(description = "Fecha y hora de expiración de la reserva (si aplica)")
        LocalDateTime reservationExpiresAt
) {}
