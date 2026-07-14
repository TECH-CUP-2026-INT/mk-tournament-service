package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.service.SanctionType;

public interface ApplySanctionUseCase {

    PlayerSanction apply(ApplySanctionCommand command);

    record ApplySanctionCommand(String playerId, SanctionType type, Integer matchesSuspended) {}
}
