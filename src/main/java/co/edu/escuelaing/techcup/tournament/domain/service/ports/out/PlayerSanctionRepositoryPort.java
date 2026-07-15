package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;

import java.util.List;

public interface PlayerSanctionRepositoryPort {
    PlayerSanction save(PlayerSanction sanction);
    List<PlayerSanction> findActiveByPlayerId(String playerId);
    List<PlayerSanction> findAllActive();
}
