package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.PaymentOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "A team with a slot reserved while its enrollment payment is still being processed.")
public record ReservedTeamResponse(
        @Schema(description = "Team ID.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID teamId,
        @Schema(description = "Team name.", example = "Los Compiladores") String teamName,
        @Schema(description = "Enrollment ID.", example = "6f9619ff-8b86-d011-b42d-00cf4fc964ff") UUID enrollmentId,
        @Schema(description = "Live payment status fetched from payment-service.", example = "PENDING")
        PaymentOrderStatus paymentOrderStatus,
        @Schema(description = "Date/time the slot reservation expires if payment isn't confirmed by then.")
        LocalDateTime reservationExpiresAt
) {}
