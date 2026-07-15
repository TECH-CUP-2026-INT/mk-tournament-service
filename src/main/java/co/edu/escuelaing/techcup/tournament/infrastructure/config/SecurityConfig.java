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
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // TC-40: /audit-events debería exigir rol Admin/Organizer, pero no existe
                        // ningún sistema de autenticación/roles en el proyecto todavía. Queda
                        // sin control de acceso real, pendiente del futuro Servicio de Identidad
                        // (JWT + roles). NO debería quedar público en producción.
                        .requestMatchers("/tournaments/**", "/matches/**", "/sanctions/**", "/audit-events/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
