package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.service.ports.ApplySanctionUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.PlayerSanctionRepositoryPort;
import co.edu.escuelaing.techcup.tournament.service.ports.SanctionNotificationPort;
import org.springframework.stereotype.Service;

@Service
public class ApplySanctionService implements ApplySanctionUseCase {

    private final PlayerSanctionRepositoryPort repository;
    private final SanctionNotificationPort notificationPort;

    public ApplySanctionService(PlayerSanctionRepositoryPort repository,
                                 SanctionNotificationPort notificationPort) {
        this.repository = repository;
        this.notificationPort = notificationPort;
    }

    @Override
    public PlayerSanction apply(ApplySanctionCommand command) {
        PlayerSanction sanction = PlayerSanction.create(
                command.playerId(), command.type(), command.matchesSuspended());

        PlayerSanction saved = repository.save(sanction);

        notificationPort.notifyPlayerSanctioned(saved.getPlayerId(), saved.getType(), saved.getMatchesRemaining());

        return saved;
    }
}
