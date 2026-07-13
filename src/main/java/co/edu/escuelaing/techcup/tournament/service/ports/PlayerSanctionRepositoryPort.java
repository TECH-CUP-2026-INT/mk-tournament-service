package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;

import java.util.List;

public interface PlayerSanctionRepositoryPort {
    PlayerSanction save(PlayerSanction sanction);
    List<PlayerSanction> findActiveByPlayerId(String playerId);
    List<PlayerSanction> findAllActive();
}
