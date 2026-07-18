package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;

import java.util.UUID;

/**
 * Puerto para notificar al jugador sancionado (integración pendiente, sin
 * implementación real todavía más allá de un log).
 */
public interface SanctionNotificationPort {
    void notifyPlayerSanctioned(UUID playerId, SanctionType type, int matchesSuspended);
}
