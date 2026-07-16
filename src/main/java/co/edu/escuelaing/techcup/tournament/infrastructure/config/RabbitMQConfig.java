package co.edu.escuelaing.techcup.tournament.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el ecosistema TechCup. Exchange compartido:
 * {@code techcup.exchange} (topic) — todos los servicios publican/consumen
 * ahí. Este servicio (Torneos) solo publica, no declara colas propias; las
 * colas y bindings de consumo son responsabilidad de cada servicio consumidor
 * (ver {@code docs/rabbitmq-integration.md} del Servicio de Estadísticas).
 */
@Configuration
public class RabbitMQConfig {

    public static final String TECHCUP_EXCHANGE = "techcup.exchange";

    @Bean
    public TopicExchange techcupExchange() {
        return new TopicExchange(TECHCUP_EXCHANGE);
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
