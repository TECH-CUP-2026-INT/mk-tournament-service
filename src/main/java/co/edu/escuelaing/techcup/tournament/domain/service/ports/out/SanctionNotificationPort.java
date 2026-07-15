package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;

/**
 * Puerto para notificar al jugador sancionado (integración pendiente, sin
 * implementación real todavía más allá de un log).
 */
public interface SanctionNotificationPort {
    void notifyPlayerSanctioned(String playerId, SanctionType type, int matchesSuspended);
}
