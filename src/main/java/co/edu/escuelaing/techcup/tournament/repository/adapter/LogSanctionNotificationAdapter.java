package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.service.SanctionType;
import co.edu.escuelaing.techcup.tournament.service.ports.SanctionNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementación local de {@link SanctionNotificationPort} usada mientras se
 * define el proveedor real de notificaciones (email/SMS/push, TBD). Se
 * reemplaza por un adapter real implementando este mismo puerto, sin tocar
 * el dominio ni el use case — mismo criterio que RandomFixtureGenerationAdapter.
 */
@Component
public class LogSanctionNotificationAdapter implements SanctionNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LogSanctionNotificationAdapter.class);

    @Override
    public void notifyPlayerSanctioned(String playerId, SanctionType type, int matchesSuspended) {
        log.info("Notificación al jugador '{}': sancionado ({}) por {} partido(s)",
                playerId, type, matchesSuspended);
    }
}
