package co.edu.escuelaing.techcup.tournament.infrastructure.out.feign;

import co.edu.escuelaing.techcup.tournament.domain.exception.PaymentOrderCreationFailedException;
import co.edu.escuelaing.techcup.tournament.domain.model.PaymentOrderStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PaymentServiceClientPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentServiceClientAdapterTest {

    private final UUID enrollmentId = UUID.randomUUID();
    private final UUID teamId = UUID.randomUUID();
    private final UUID tournamentId = UUID.randomUUID();

    @Test
    void getOrderStatus_cuandoElFeignClientResponde_retornaElStatus() {
        PaymentServiceFeignClient feignClient = mock(PaymentServiceFeignClient.class);
        when(feignClient.getOrderStatus(enrollmentId))
                .thenReturn(new PaymentServiceFeignClient.PaymentOrderResponse(PaymentOrderStatus.APPROVED));

        PaymentServiceClientAdapter adapter = new PaymentServiceClientAdapter(feignClient);

        assertEquals(PaymentOrderStatus.APPROVED, adapter.getOrderStatus(enrollmentId));
    }

    @Test
    void getOrderStatus_cuandoElFeignClientFalla_retornaUnknown() {
        PaymentServiceFeignClient feignClient = mock(PaymentServiceFeignClient.class);
        when(feignClient.getOrderStatus(enrollmentId)).thenThrow(new RuntimeException("timeout"));

        PaymentServiceClientAdapter adapter = new PaymentServiceClientAdapter(feignClient);

        assertEquals(PaymentOrderStatus.UNKNOWN, adapter.getOrderStatus(enrollmentId));
    }

    @Test
    void createOrder_cuandoElFeignClientResponde_retornaLaReferencia() {
        PaymentServiceFeignClient feignClient = mock(PaymentServiceFeignClient.class);
        when(feignClient.createOrder(any()))
                .thenReturn(new PaymentServiceFeignClient.CreateOrderResponse("po1", PaymentOrderStatus.PENDING));

        PaymentServiceClientAdapter adapter = new PaymentServiceClientAdapter(feignClient);

        PaymentServiceClientPort.PaymentOrderReference result =
                adapter.createOrder(enrollmentId, teamId, tournamentId, BigDecimal.TEN);

        assertEquals("po1", result.paymentOrderId());
        assertEquals(PaymentOrderStatus.PENDING, result.status());
    }

    @Test
    void createOrder_cuandoElFeignClientFalla_lanzaPaymentOrderCreationFailedException() {
        PaymentServiceFeignClient feignClient = mock(PaymentServiceFeignClient.class);
        when(feignClient.createOrder(any())).thenThrow(new RuntimeException("connection refused"));

        PaymentServiceClientAdapter adapter = new PaymentServiceClientAdapter(feignClient);

        assertThrows(PaymentOrderCreationFailedException.class,
                () -> adapter.createOrder(enrollmentId, teamId, tournamentId, BigDecimal.TEN));
    }
}
