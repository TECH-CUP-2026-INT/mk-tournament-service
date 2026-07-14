package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.entity.document.PlayerSanctionDocument;
import co.edu.escuelaing.techcup.tournament.repository.mongo.PlayerSanctionMongoRepository;
import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.service.SanctionType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerSanctionRepositoryAdapterTest {

    @Test
    void save_delegaAlMongoRepositoryYMapeaDeVuelta() {
        PlayerSanctionMongoRepository mongoRepository = mock(PlayerSanctionMongoRepository.class);
        PlayerSanction sanction = PlayerSanction.create("player1", SanctionType.RED_CARD, null);
        PlayerSanctionDocument saved = new PlayerSanctionDocument("s1", "player1", "RED_CARD", 1);
        when(mongoRepository.save(any())).thenReturn(saved);

        PlayerSanctionRepositoryAdapter adapter = new PlayerSanctionRepositoryAdapter(mongoRepository);
        PlayerSanction result = adapter.save(sanction);

        assertEquals("s1", result.getId());
    }

    @Test
    void findActiveByPlayerId_retornaSancionesActivas() {
        PlayerSanctionMongoRepository mongoRepository = mock(PlayerSanctionMongoRepository.class);
        PlayerSanctionDocument document = new PlayerSanctionDocument("s1", "player1", "RED_CARD", 1);
        when(mongoRepository.findByPlayerIdAndMatchesRemainingGreaterThan("player1", 0))
                .thenReturn(List.of(document));

        PlayerSanctionRepositoryAdapter adapter = new PlayerSanctionRepositoryAdapter(mongoRepository);
        List<PlayerSanction> result = adapter.findActiveByPlayerId("player1");

        assertEquals(1, result.size());
        assertEquals("player1", result.get(0).getPlayerId());
    }

    @Test
    void findAllActive_retornaTodasLasSancionesActivas() {
        PlayerSanctionMongoRepository mongoRepository = mock(PlayerSanctionMongoRepository.class);
        PlayerSanctionDocument document = new PlayerSanctionDocument("s1", "player1", "YELLOW_CARD_ACCUMULATION", 1);
        when(mongoRepository.findByMatchesRemainingGreaterThan(0)).thenReturn(List.of(document));

        PlayerSanctionRepositoryAdapter adapter = new PlayerSanctionRepositoryAdapter(mongoRepository);
        List<PlayerSanction> result = adapter.findAllActive();

        assertEquals(1, result.size());
    }
}
