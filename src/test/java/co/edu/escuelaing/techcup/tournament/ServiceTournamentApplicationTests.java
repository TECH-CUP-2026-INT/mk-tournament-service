package co.edu.escuelaing.techcup.tournament;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * El contenedor del listener de RabbitMQ ({@code MatchFinishedListener}) se
 * desactiva aquí a propósito: {@code BlockingQueueConsumer.start()} trata
 * cualquier fallo de autenticación contra el broker como fatal para el
 * arranque del contexto (no hay forma de hacerlo "best-effort" desde
 * configuración de Spring AMQP), y este test solo valida que el contexto
 * de Spring cargue correctamente — no que la mensajería funcione end to
 * end (eso ya lo cubren {@code MatchFinishedListenerTest} y el smoke test
 * de Docker). Sin este auto-startup=false, este test queda rehén de que
 * el broker (CloudAMQP) esté arriba y las credenciales sean válidas.
 */
@SpringBootTest
@TestPropertySource(properties = "spring.rabbitmq.listener.simple.auto-startup=false")
class ServiceTournamentApplicationTests {

	@Test
	void contextLoads() {
	}

}
