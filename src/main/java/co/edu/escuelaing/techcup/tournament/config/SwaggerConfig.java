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
                                REST API for the tournament management microservice of the TechCup Football platform.

                                Covers the full tournament lifecycle: creation, editing, pausing, finalizing and
                                inactivating tournaments; team enrollment; scheduling matches with courts and referees;
                                bracket/group/league fixture generation; player sanctions; champion assignment; and an
                                audit trail of every action performed through the API.

                                **Conventions**
                                - All endpoints are currently open (`permitAll()`) — there is no Identity Service with
                                  JWT/roles yet. Role names shown per endpoint describe the intended business owner of
                                  the action, not an enforced restriction.
                                - Timestamps use ISO-8601 (`yyyy-MM-dd` for dates, `HH:mm:ss` for times).
                                - `4xx` responses signal invalid input or a state conflict; `5xx` responses signal an
                                  unexpected internal failure.

                                **External dependencies**
                                - `payment-service` at `http://localhost:8081` — validates enrollment payments.
                                - `team-service` at `http://localhost:8082` — resolves team data.
                                """)
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Team MK — TechCup 2026 INT")
                                .email("hever.barrera@mail.escuelaing.edu.co"))
                        .license(new License()
                                .name("Internal academic use")
                                .url("https://github.com/TECH-CUP-2026-INT/mk-tournament-service")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development"),
                        new Server().url("https://api.techcup.escuelaing.edu.co").description("Production (pending)")
                ));
    }
}
