package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PlayerSanctionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ViewPlayerSanctionServiceTest {

    @Test
    void getActiveSanctions_retornaLasSancionesActivasDelJugador() {
        PlayerSanctionRepositoryPort repository = mock(PlayerSanctionRepositoryPort.class);
        PlayerSanction sanction = PlayerSanction.reconstruct("s1", "p1", SanctionType.RED_CARD, 1);

        when(repository.findActiveByPlayerId("p1")).thenReturn(List.of(sanction));

        ViewPlayerSanctionService service = new ViewPlayerSanctionService(repository);

        List<PlayerSanction> result = service.getActiveSanctions("p1");

        assertEquals(1, result.size());
        assertEquals("p1", result.get(0).getPlayerId());
    }
}
