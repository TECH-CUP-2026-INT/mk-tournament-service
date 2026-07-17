package co.edu.escuelaing.techcup.tournament.infrastructure.out.feign;

import co.edu.escuelaing.techcup.tournament.domain.exception.MatchDefinitionPushFailedException;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.MatchDefinitionPort;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Cliente hacia Matches, vía Feign ({@link MatchesServiceFeignClient}). A
 * diferencia de Team/Payment Service (best-effort o con fallback), este envío
 * es crítico: cualquier falla que no sea "409 ya no editable" se propaga como
 * {@link MatchDefinitionPushFailedException} para que el llamador marque el
 * partido pendiente de reenvío (ver ScheduleMatchService).
 */
@Component
public class MatchDefinitionRestAdapter implements MatchDefinitionPort {

    private static final Logger log = LoggerFactory.getLogger(MatchDefinitionRestAdapter.class);

    private final MatchesServiceFeignClient feignClient;
    private final String internalApiKey;

    public MatchDefinitionRestAdapter(
            MatchesServiceFeignClient feignClient,
            @Value("${matches-service.internal-api-key:}") String internalApiKey) {
        this.feignClient = feignClient;
        this.internalApiKey = internalApiKey;
    }

    @Override
    public void sendDefinition(MatchDefinition definition) {
        try {
            feignClient.createOrUpdateMatch(internalApiKey, toRequest(definition));
        } catch (FeignException.Conflict ex) {
            log.info("Partido '{}' ya no es editable en Matches (409: probablemente ya inició o finalizó); "
                    + "se ignora, no requiere reenvío.", definition.matchId());
        } catch (RuntimeException ex) {
            log.error("No se pudo enviar la definición del partido '{}' (torneo '{}') a Matches; "
                    + "queda pendiente de reenvío manual.", definition.matchId(), definition.tournamentId(), ex);
            throw new MatchDefinitionPushFailedException(definition.matchId(), ex);
        }
    }

    @Override
    public void notifyMatchInactivated(UUID matchId) {
        // Ruta no acordada todavía con el equipo de Matches (¿DELETE /api/partidos/{matchId}?
        // ¿reenvío con un estado de "inactivo"?). Deliberadamente no se inventa un endpoint:
        // se deja constancia en el log hasta que se confirme el contrato.
        log.warn("Partido '{}' inactivado: notificación a Matches pendiente de confirmar "
                + "(endpoint aún no acordado, no se realizó ninguna llamada HTTP).", matchId);
    }

    private MatchesServiceFeignClient.MatchDefinitionRequest toRequest(MatchDefinition definition) {
        return new MatchesServiceFeignClient.MatchDefinitionRequest(
                definition.matchId(),
                definition.tournamentId(),
                definition.fase() != null ? definition.fase().name() : null,
                definition.equipoAId(),
                definition.equipoBId(),
                definition.equipoANombre(),
                definition.equipoBNombre(),
                definition.fecha(),
                definition.hora(),
                definition.arbitroId(),
                definition.canchaId());
    }
}
