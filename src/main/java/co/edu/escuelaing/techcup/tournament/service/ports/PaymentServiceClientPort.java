package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.PaymentOrderStatus;

public interface PaymentServiceClientPort {

    /**
     * Consulta en vivo el estado de la PaymentOrder asociada a un Enrollment.
     * Nunca lanza excepción: si el Payment Service no responde a tiempo o falla,
     * debe devolver {@link PaymentOrderStatus#UNKNOWN}.
     */
    PaymentOrderStatus getOrderStatus(String enrollmentId);
}
