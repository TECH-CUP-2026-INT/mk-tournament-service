package co.edu.escuelaing.techcup.tournament.infrastructure.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Cliente Feign hacia Matches. Contrato acordado: POST /api/partidos, upsert
 * por matchId mientras el partido siga SCHEDULED del lado de Matches; header
 * {@code X-Internal-Api-Key} obligatorio.
 */
@FeignClient(name = "matches-service", url = "${matches-service.base-url}")
public interface MatchesServiceFeignClient {

    @PostMapping("/api/partidos")
    ResponseEntity<Void> createOrUpdateMatch(
            @RequestHeader("X-Internal-Api-Key") String apiKey,
            @RequestBody MatchDefinitionRequest request);

    record MatchDefinitionRequest(
            UUID matchId,
            UUID tournamentId,
            String fase,
            UUID equipoAId,
            UUID equipoBId,
            String equipoANombre,
            String equipoBNombre,
            LocalDate fecha,
            LocalTime hora,
            UUID arbitroId,
            UUID canchaId) {}
}
