package co.edu.escuelaing.techcup.tournament.infrastructure.out.feign;

import co.edu.escuelaing.techcup.tournament.domain.model.PaymentOrderStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cliente Feign hacia el Payment Service. El contrato exacto de esa API aún
 * no está definido/versionado en ese repo; se asume el mismo shape que ya
 * usaba el cliente RestClient anterior (GET/POST /payment-orders).
 */
@FeignClient(name = "payment-service", url = "${payment-service.base-url}")
public interface PaymentServiceFeignClient {

    @GetMapping("/payment-orders/{enrollmentId}")
    PaymentOrderResponse getOrderStatus(@PathVariable("enrollmentId") UUID enrollmentId);

    @PostMapping("/payment-orders")
    CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request);

    record PaymentOrderResponse(PaymentOrderStatus status) {}

    record CreateOrderRequest(UUID enrollmentId, UUID teamId, UUID tournamentId, BigDecimal amount) {}

    record CreateOrderResponse(String paymentOrderId, PaymentOrderStatus status) {}
}
