package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.exception.PaymentOrderCreationFailedException;
import co.edu.escuelaing.techcup.tournament.repository.feign.PaymentServiceFeignClient;
import co.edu.escuelaing.techcup.tournament.service.PaymentOrderStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.PaymentServiceClientPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentServiceClientAdapterTest {

    @Test
    void getOrderStatus_cuandoElFeignClientResponde_retornaElStatus() {
        PaymentServiceFeignClient feignClient = mock(PaymentServiceFeignClient.class);
        when(feignClient.getOrderStatus("enr1"))
                .thenReturn(new PaymentServiceFeignClient.PaymentOrderResponse(PaymentOrderStatus.APPROVED));

        PaymentServiceClientAdapter adapter = new PaymentServiceClientAdapter(feignClient);

        assertEquals(PaymentOrderStatus.APPROVED, adapter.getOrderStatus("enr1"));
    }

    @Test
    void getOrderStatus_cuandoElFeignClientFalla_retornaUnknown() {
        PaymentServiceFeignClient feignClient = mock(PaymentServiceFeignClient.class);
        when(feignClient.getOrderStatus("enr1")).thenThrow(new RuntimeException("timeout"));

        PaymentServiceClientAdapter adapter = new PaymentServiceClientAdapter(feignClient);

        assertEquals(PaymentOrderStatus.UNKNOWN, adapter.getOrderStatus("enr1"));
    }

    @Test
    void createOrder_cuandoElFeignClientResponde_retornaLaReferencia() {
        PaymentServiceFeignClient feignClient = mock(PaymentServiceFeignClient.class);
        when(feignClient.createOrder(any()))
                .thenReturn(new PaymentServiceFeignClient.CreateOrderResponse("po1", PaymentOrderStatus.PENDING));

        PaymentServiceClientAdapter adapter = new PaymentServiceClientAdapter(feignClient);

        PaymentServiceClientPort.PaymentOrderReference result =
                adapter.createOrder("enr1", "team1", "t1", BigDecimal.TEN);

        assertEquals("po1", result.paymentOrderId());
        assertEquals(PaymentOrderStatus.PENDING, result.status());
    }

    @Test
    void createOrder_cuandoElFeignClientFalla_lanzaPaymentOrderCreationFailedException() {
        PaymentServiceFeignClient feignClient = mock(PaymentServiceFeignClient.class);
        when(feignClient.createOrder(any())).thenThrow(new RuntimeException("connection refused"));

        PaymentServiceClientAdapter adapter = new PaymentServiceClientAdapter(feignClient);

        assertThrows(PaymentOrderCreationFailedException.class,
                () -> adapter.createOrder("enr1", "team1", "t1", BigDecimal.TEN));
    }
}
