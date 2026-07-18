package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.PlayerSanctionDocument;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.PlayerSanctionMongoRepository;
import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerSanctionRepositoryAdapterTest {

    private final co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.PlayerSanctionPersistenceMapper mapper = Mappers.getMapper(co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.PlayerSanctionPersistenceMapper.class);

    @Test
    void save_delegaAlMongoRepositoryYMapeaDeVuelta() {
        UUID sanctionId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        PlayerSanctionMongoRepository mongoRepository = mock(PlayerSanctionMongoRepository.class);
        PlayerSanction sanction = PlayerSanction.create(playerId, SanctionType.RED_CARD, null);
        PlayerSanctionDocument saved = new PlayerSanctionDocument(sanctionId, playerId, "RED_CARD", 1);
        when(mongoRepository.save(any())).thenReturn(saved);

        PlayerSanctionRepositoryAdapter adapter = new PlayerSanctionRepositoryAdapter(mongoRepository, mapper);
        PlayerSanction result = adapter.save(sanction);

        assertEquals(sanctionId, result.getId());
    }

    @Test
    void findActiveByPlayerId_retornaSancionesActivas() {
        UUID sanctionId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        PlayerSanctionMongoRepository mongoRepository = mock(PlayerSanctionMongoRepository.class);
        PlayerSanctionDocument document = new PlayerSanctionDocument(sanctionId, playerId, "RED_CARD", 1);
        when(mongoRepository.findByPlayerIdAndMatchesRemainingGreaterThan(playerId, 0))
                .thenReturn(List.of(document));

        PlayerSanctionRepositoryAdapter adapter = new PlayerSanctionRepositoryAdapter(mongoRepository, mapper);
        List<PlayerSanction> result = adapter.findActiveByPlayerId(playerId);

        assertEquals(1, result.size());
        assertEquals(playerId, result.get(0).getPlayerId());
    }

    @Test
    void findAllActive_retornaTodasLasSancionesActivas() {
        PlayerSanctionMongoRepository mongoRepository = mock(PlayerSanctionMongoRepository.class);
        PlayerSanctionDocument document = new PlayerSanctionDocument(UUID.randomUUID(), UUID.randomUUID(), "YELLOW_CARD_ACCUMULATION", 1);
        when(mongoRepository.findByMatchesRemainingGreaterThan(0)).thenReturn(List.of(document));

        PlayerSanctionRepositoryAdapter adapter = new PlayerSanctionRepositoryAdapter(mongoRepository, mapper);
        List<PlayerSanction> result = adapter.findAllActive();

        assertEquals(1, result.size());
    }
}
