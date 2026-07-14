package co.edu.escuelaing.techcup.tournament.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI techCupOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TechCup — Tournament Service API")
                        .description("""
                                API REST del microservicio de gestión de torneos de la plataforma TechCup Fútbol.
                                Permite crear, editar, pausar, finalizar e inactivar torneos;
                                inscribir equipos; gestionar canchas, árbitros, sanciones y el historial de eventos.

                                **Servicios externos requeridos:**
                                - `payment-service` en `http://localhost:8081` — validación de pagos de inscripción.
                                - `team-service` en `http://localhost:8082` — consulta de datos del equipo.
                                """)
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Equipo MK — TechCup 2026 INT")
                                .email("hever.barrera@mail.escuelaing.edu.co"))
                        .license(new License()
                                .name("Uso académico interno")
                                .url("https://github.com/TECH-CUP-2026-INT/mk-tournament-service")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Desarrollo local"),
                        new Server().url("https://api.techcup.escuelaing.edu.co").description("Producción (pendiente)")
                ));
    }
}
