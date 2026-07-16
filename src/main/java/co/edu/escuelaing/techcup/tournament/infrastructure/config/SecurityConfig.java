package co.edu.escuelaing.techcup.tournament.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // No existe ningún sistema de autenticación/roles en el proyecto todavía
        // (TC-40: /audit-events debería exigir rol Admin/Organizer, otros consumidores
        // como el Team Service no deberían necesitar credenciales para consultarnos).
        // Todo queda sin control de acceso real, pendiente del futuro Servicio de
        // Identidad (JWT + roles). NO debería quedar público en producción.
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
