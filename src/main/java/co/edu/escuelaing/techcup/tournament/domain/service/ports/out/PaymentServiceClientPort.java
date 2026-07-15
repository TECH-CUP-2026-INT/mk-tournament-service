package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;

import co.edu.escuelaing.techcup.tournament.domain.exception.PaymentOrderCreationFailedException;
import co.edu.escuelaing.techcup.tournament.domain.model.PaymentOrderStatus;

import java.math.BigDecimal;

/**
 * Puerto hacia el Payment Service. Nótese la asimetría entre sus dos
 * métodos: uno degrada a {@link co.edu.escuelaing.techcup.tournament.domain.model.PaymentOrderStatus#UNKNOWN}
 * si el servicio externo falla, el otro propaga la falla — ver el
 * javadoc de cada método.
 */
public interface PaymentServiceClientPort {

    /**
     * Consulta en vivo el estado de la PaymentOrder asociada a un Enrollment.
     * Nunca lanza excepción: si el Payment Service no responde a tiempo o falla,
     * debe devolver {@link PaymentOrderStatus#UNKNOWN}.
     */
    PaymentOrderStatus getOrderStatus(String enrollmentId);

    /**
     * Crea la PaymentOrder asociada a un Enrollment recién reservado.
     * A diferencia de {@link #getOrderStatus(String)}, SÍ propaga el error
     * ({@link PaymentOrderCreationFailedException}) si el Payment Service no
     * responde: si la orden no se crea, la inscripción no debe quedar huérfana.
     */
    PaymentOrderReference createOrder(String enrollmentId, String teamId, String tournamentId, BigDecimal amount);

    record PaymentOrderReference(String paymentOrderId, PaymentOrderStatus status) {}
}
