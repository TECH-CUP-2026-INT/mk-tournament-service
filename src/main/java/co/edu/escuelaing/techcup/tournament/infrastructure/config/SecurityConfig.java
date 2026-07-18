package co.edu.escuelaing.techcup.tournament.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Decision intencional, no un descuido: no existe ningun sistema de
     * autenticacion/roles en el proyecto todavia (TC-40: /audit-events deberia
     * exigir rol Admin/Organizer; otros consumidores como el Team Service no
     * deberian necesitar credenciales para consultarnos); no hay control de
     * acceso real, bloqueado por el futuro Servicio de Identidad (JWT + roles).
     * Pendiente de retirar esta configuracion antes de producción real.
     */
    @Bean
    @SuppressWarnings("java:S4502") // CSRF deshabilitado a proposito: ver justificacion en el javadoc de este metodo
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
