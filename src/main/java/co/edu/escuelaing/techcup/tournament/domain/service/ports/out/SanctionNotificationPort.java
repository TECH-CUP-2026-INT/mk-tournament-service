package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;

public interface SanctionNotificationPort {
    void notifyPlayerSanctioned(String playerId, SanctionType type, int matchesSuspended);
}
