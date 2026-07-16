package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;

import java.util.UUID;

public interface ApplySanctionUseCase {

    PlayerSanction apply(ApplySanctionCommand command);

    record ApplySanctionCommand(UUID playerId, SanctionType type, Integer matchesSuspended) {}
}
