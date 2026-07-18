package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PlayerSanctionRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewPlayerSanctionUseCase;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ViewPlayerSanctionService implements ViewPlayerSanctionUseCase {

    private final PlayerSanctionRepositoryPort repository;


    @Override
    public List<PlayerSanction> getActiveSanctions(UUID playerId) {
        return repository.findActiveByPlayerId(playerId);
    }
}
