package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ApplySanctionUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PlayerSanctionRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.SanctionNotificationPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplySanctionService implements ApplySanctionUseCase {

    private final PlayerSanctionRepositoryPort repository;
    private final SanctionNotificationPort notificationPort;


    @Override
    public PlayerSanction apply(ApplySanctionCommand command) {
        PlayerSanction sanction = PlayerSanction.create(
                command.playerId(), command.type(), command.matchesSuspended());

        PlayerSanction saved = repository.save(sanction);

        notificationPort.notifyPlayerSanctioned(saved.getPlayerId(), saved.getType(), saved.getMatchesRemaining());

        return saved;
    }
}
