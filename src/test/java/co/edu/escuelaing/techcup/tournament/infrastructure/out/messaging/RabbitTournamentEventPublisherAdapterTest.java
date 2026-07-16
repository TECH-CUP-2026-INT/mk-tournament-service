package co.edu.escuelaing.techcup.tournament.infrastructure.out.messaging;

import co.edu.escuelaing.techcup.tournament.infrastructure.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitTournamentEventPublisherAdapterTest {

    @Test
    void publishTournamentFinalized_enviaAlExchangeCompartidoConLaRoutingKeyCorrecta() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        RabbitTournamentEventPublisherAdapter adapter = new RabbitTournamentEventPublisherAdapter(rabbitTemplate);

        adapter.publishTournamentFinalized(UUID.randomUUID());

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.TECHCUP_EXCHANGE), eq("techcup.tournament.event.finalized"), any(Object.class));
    }

    @Test
    void publishTournamentFinalized_cuandoElBrokerFalla_noPropagaLaExcepcion() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        doThrow(new AmqpException("broker no disponible"))
                .when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        RabbitTournamentEventPublisherAdapter adapter = new RabbitTournamentEventPublisherAdapter(rabbitTemplate);

        assertDoesNotThrow(() -> adapter.publishTournamentFinalized(UUID.randomUUID()));
    }
}
