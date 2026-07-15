package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;

import java.util.List;

public interface ViewPlayerSanctionUseCase {
    List<PlayerSanction> getActiveSanctions(String playerId);
}
