package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.SanctionType;

public interface SanctionNotificationPort {
    void notifyPlayerSanctioned(String playerId, SanctionType type, int matchesSuspended);
}
