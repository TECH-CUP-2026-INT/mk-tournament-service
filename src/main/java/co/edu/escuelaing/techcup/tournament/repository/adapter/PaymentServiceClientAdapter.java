package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.exception.PaymentOrderCreationFailedException;
import co.edu.escuelaing.techcup.tournament.service.PaymentOrderStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.PaymentServiceClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

/**
 * Cliente HTTP hacia el Payment Service (GET /payment-orders/{enrollmentId}).
 * El contrato exacto de esa API aún no está definido en este repo, así que
 * mientras no exista un Payment Service real desplegado en {@code payment-service.base-url},
 * cualquier llamada cae en timeout/conexión rechazada y este adaptador
 * responde {@link PaymentOrderStatus#UNKNOWN}, cumpliendo la regla de negocio
 * TC-109 #4 (nunca falla la consulta completa por un ítem no resuelto).
 */
@Component
public class PaymentServiceClientAdapter implements PaymentServiceClientPort {

    private static final int TIMEOUT_MILLIS = 2500;

    private final RestClient restClient;

    public PaymentServiceClientAdapter(
            @Value("${payment-service.base-url:http://localhost:8081}") String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT_MILLIS);
        requestFactory.setReadTimeout(TIMEOUT_MILLIS);
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public PaymentOrderStatus getOrderStatus(String enrollmentId) {
        try {
            PaymentOrderResponse response = restClient.get()
                    .uri("/payment-orders/{enrollmentId}", enrollmentId)
                    .retrieve()
                    .body(PaymentOrderResponse.class);
            return response != null && response.status() != null ? response.status() : PaymentOrderStatus.UNKNOWN;
        } catch (Exception ex) {
            return PaymentOrderStatus.UNKNOWN;
        }
    }

    @Override
    public PaymentOrderReference createOrder(String enrollmentId, String teamId, String tournamentId, BigDecimal amount) {
        try {
            CreateOrderResponse response = restClient.post()
                    .uri("/payment-orders")
                    .body(new CreateOrderRequest(enrollmentId, teamId, tournamentId, amount))
                    .retrieve()
                    .body(CreateOrderResponse.class);
            if (response == null) {
                throw new IllegalStateException("Respuesta vacía del Payment Service");
            }
            return new PaymentOrderReference(response.paymentOrderId(), response.status());
        } catch (Exception ex) {
            throw new PaymentOrderCreationFailedException(enrollmentId, ex);
        }
    }

    private record PaymentOrderResponse(PaymentOrderStatus status) {}

    private record CreateOrderRequest(String enrollmentId, String teamId, String tournamentId, BigDecimal amount) {}

    private record CreateOrderResponse(String paymentOrderId, PaymentOrderStatus status) {}
}
