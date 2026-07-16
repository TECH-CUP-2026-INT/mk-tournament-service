package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;

import java.util.List;
import java.util.UUID;

public interface ViewPlayerSanctionUseCase {
    List<PlayerSanction> getActiveSanctions(UUID playerId);
}
