package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;

import co.edu.escuelaing.techcup.tournament.domain.model.PaymentOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "A team with a slot reserved while its enrollment payment is still being processed.")
public record ReservedTeamResponse(
        @Schema(description = "Team ID.", example = "team_xyz789") String teamId,
        @Schema(description = "Team name.", example = "Los Compiladores") String teamName,
        @Schema(description = "Enrollment ID.", example = "enr_abc123") String enrollmentId,
        @Schema(description = "Live payment status fetched from payment-service.", example = "PENDING")
        PaymentOrderStatus paymentOrderStatus,
        @Schema(description = "Date/time the slot reservation expires if payment isn't confirmed by then.")
        LocalDateTime reservationExpiresAt
) {}
