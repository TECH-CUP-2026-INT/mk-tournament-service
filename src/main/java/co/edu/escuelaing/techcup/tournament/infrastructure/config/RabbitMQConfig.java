package co.edu.escuelaing.techcup.tournament.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el ecosistema TechCup. Exchange compartido
 * (topic) — todos los servicios publican/consumen ahí.
 * <p>
 * DIVERGENCIA PENDIENTE DE CONFIRMAR: el {@code docs/rabbitmq-integration.md}
 * del Servicio de Estadísticas (no accesible desde este repo) reportaría
 * "techcup.events" como nombre canónico del exchange, mientras este servicio
 * venía declarando "techcup.exchange". No se asume cuál es el correcto: el
 * nombre queda configurable por la env var {@code TECHCUP_RABBITMQ_EXCHANGE},
 * con "techcup.exchange" como default hasta que se confirme contra ese doc.
 * <p>
 * Este servicio históricamente solo publicaba (finalización de torneo). Desde
 * ProcessMatchResult (paso 4) también consume: declara una cola propia
 * bindeada a la routing key {@code techcup.match.finished}, con el mismo
 * durable=true/autoDelete=false que usa Matches (si difiere, RabbitMQ
 * responde PRECONDITION_FAILED al declarar el exchange).
 */
@Configuration
public class RabbitMQConfig {

    public static final String DEFAULT_EXCHANGE = "techcup.exchange";
    public static final String MATCH_FINISHED_ROUTING_KEY = "techcup.match.finished";
    public static final String MATCH_FINISHED_QUEUE = "techcup.tournament.match-finished";

    @Value("${techcup.rabbitmq.exchange:" + DEFAULT_EXCHANGE + "}")
    private String exchangeName;

    @Bean
    public TopicExchange techcupExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue matchFinishedQueue() {
        return new Queue(MATCH_FINISHED_QUEUE, true, false, false);
    }

    @Bean
    public Binding matchFinishedBinding(Queue matchFinishedQueue, TopicExchange techcupExchange) {
        return BindingBuilder.bind(matchFinishedQueue).to(techcupExchange).with(MATCH_FINISHED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
