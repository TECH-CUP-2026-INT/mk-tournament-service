package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;

public interface ApplySanctionUseCase {

    PlayerSanction apply(ApplySanctionCommand command);

    record ApplySanctionCommand(String playerId, SanctionType type, Integer matchesSuspended) {}
}
