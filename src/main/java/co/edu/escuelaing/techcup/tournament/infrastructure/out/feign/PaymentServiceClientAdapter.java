package co.edu.escuelaing.techcup.tournament.infrastructure.out.feign;

import co.edu.escuelaing.techcup.tournament.domain.exception.PaymentOrderCreationFailedException;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.feign.PaymentServiceFeignClient;
import co.edu.escuelaing.techcup.tournament.domain.model.PaymentOrderStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PaymentServiceClientPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cliente hacia el Payment Service, vía Feign ({@link PaymentServiceFeignClient}).
 * El contrato exacto de esa API aún no está definido en este repo, así que
 * mientras no exista un Payment Service real desplegado en {@code payment-service.base-url},
 * cualquier llamada cae en timeout/conexión rechazada y este adaptador
 * responde {@link PaymentOrderStatus#UNKNOWN}, cumpliendo la regla de negocio
 * TC-109 #4 (nunca falla la consulta completa por un ítem no resuelto).
 */
@Component
public class PaymentServiceClientAdapter implements PaymentServiceClientPort {

    private final PaymentServiceFeignClient feignClient;

    public PaymentServiceClientAdapter(PaymentServiceFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    public PaymentOrderStatus getOrderStatus(UUID enrollmentId) {
        try {
            PaymentServiceFeignClient.PaymentOrderResponse response = feignClient.getOrderStatus(enrollmentId);
            return response != null && response.status() != null ? response.status() : PaymentOrderStatus.UNKNOWN;
        } catch (Exception ex) {
            return PaymentOrderStatus.UNKNOWN;
        }
    }

    @Override
    public PaymentOrderReference createOrder(UUID enrollmentId, UUID teamId, UUID tournamentId, BigDecimal amount) {
        try {
            PaymentServiceFeignClient.CreateOrderResponse response = feignClient.createOrder(
                    new PaymentServiceFeignClient.CreateOrderRequest(enrollmentId, teamId, tournamentId, amount));
            if (response == null) {
                throw new IllegalStateException("Respuesta vacía del Payment Service");
            }
            return new PaymentOrderReference(response.paymentOrderId(), response.status());
        } catch (Exception ex) {
            throw new PaymentOrderCreationFailedException(enrollmentId, ex);
        }
    }
}
