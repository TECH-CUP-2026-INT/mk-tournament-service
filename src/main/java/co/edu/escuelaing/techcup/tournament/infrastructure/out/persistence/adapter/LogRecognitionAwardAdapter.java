package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.RecognitionAwardPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementación local de {@link RecognitionAwardPort} usada mientras se
 * define el proveedor real de reconocimientos/premios (TBD). Se reemplaza
 * por un adapter real implementando este mismo puerto, sin tocar el dominio
 * ni el use case — mismo criterio que RandomFixtureGenerationAdapter y
 * LogSanctionNotificationAdapter.
 */
@Component
public class LogRecognitionAwardAdapter implements RecognitionAwardPort {

    private static final Logger log = LoggerFactory.getLogger(LogRecognitionAwardAdapter.class);

    @Override
    public void triggerAwards(String tournamentId) {
        log.info("Reconocimientos disparados para el torneo finalizado '{}'", tournamentId);
    }
}
