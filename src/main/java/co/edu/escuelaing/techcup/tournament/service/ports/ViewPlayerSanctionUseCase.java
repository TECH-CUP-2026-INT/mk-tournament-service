package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;

import java.util.List;

public interface ViewPlayerSanctionUseCase {
    List<PlayerSanction> getActiveSanctions(String playerId);
}
