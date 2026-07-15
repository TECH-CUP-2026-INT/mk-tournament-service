package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PlayerSanctionRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewPlayerSanctionUseCase;
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
