package co.edu.escuelaing.techcup.tournament.infrastructure.out.messaging;

import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentEventPublisherPort;
import co.edu.escuelaing.techcup.tournament.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RabbitTournamentEventPublisherAdapter implements TournamentEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitTournamentEventPublisherAdapter.class);
    private static final String ROUTING_KEY_FINALIZED = "techcup.tournament.event.finalized";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishTournamentFinalized(String tournamentId) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.TECHCUP_EXCHANGE, ROUTING_KEY_FINALIZED,
                    new TournamentFinalizedEvent(tournamentId, LocalDateTime.now()));
        } catch (RuntimeException e) {
            log.warn("No se pudo publicar el evento de finalización para el torneo '{}'", tournamentId, e);
        }
    }

    /** Espejo del contrato acordado con Estadísticas (docs/rabbitmq-integration.md del Servicio de Estadísticas). */
    private record TournamentFinalizedEvent(String tournamentId, LocalDateTime occurredAt) {}
}
