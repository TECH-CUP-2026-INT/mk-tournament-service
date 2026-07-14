package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.service.ports.PlayerSanctionRepositoryPort;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewPlayerSanctionUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewPlayerSanctionService implements ViewPlayerSanctionUseCase {

    private final PlayerSanctionRepositoryPort repository;

    public ViewPlayerSanctionService(PlayerSanctionRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<PlayerSanction> getActiveSanctions(String playerId) {
        return repository.findActiveByPlayerId(playerId);
    }
}
