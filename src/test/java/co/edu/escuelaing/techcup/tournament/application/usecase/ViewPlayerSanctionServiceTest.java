package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PlayerSanctionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ViewPlayerSanctionServiceTest {

    @Test
    void getActiveSanctions_retornaLasSancionesActivasDelJugador() {
        UUID playerId = UUID.randomUUID();
        PlayerSanctionRepositoryPort repository = mock(PlayerSanctionRepositoryPort.class);
        PlayerSanction sanction = PlayerSanction.reconstruct(UUID.randomUUID(), playerId, SanctionType.RED_CARD, 1);

        when(repository.findActiveByPlayerId(playerId)).thenReturn(List.of(sanction));

        ViewPlayerSanctionService service = new ViewPlayerSanctionService(repository);

        List<PlayerSanction> result = service.getActiveSanctions(playerId);

        assertEquals(1, result.size());
        assertEquals(playerId, result.get(0).getPlayerId());
    }
}
