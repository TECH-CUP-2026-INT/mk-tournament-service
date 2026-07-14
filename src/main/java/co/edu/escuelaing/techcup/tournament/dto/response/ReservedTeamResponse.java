package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.PaymentOrderStatus;

import java.time.LocalDateTime;

public record ReservedTeamResponse(
        String teamId,
        String teamName,
        String enrollmentId,
        PaymentOrderStatus paymentOrderStatus,
        LocalDateTime reservationExpiresAt
) {}
