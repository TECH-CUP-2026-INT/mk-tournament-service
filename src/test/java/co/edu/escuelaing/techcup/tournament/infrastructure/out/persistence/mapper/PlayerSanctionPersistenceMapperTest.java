package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.PlayerSanctionDocument;
import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerSanctionPersistenceMapperTest {

    private final PlayerSanctionPersistenceMapper mapper = Mappers.getMapper(PlayerSanctionPersistenceMapper.class);

    @Test
    void toDomain_convierteTodosLosCampos() {
        UUID id = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        PlayerSanctionDocument document = new PlayerSanctionDocument(id, playerId, "RED_CARD", 1);

        PlayerSanction sanction = mapper.toDomain(document);

        assertEquals(id, sanction.getId());
        assertEquals(playerId, sanction.getPlayerId());
        assertEquals(SanctionType.RED_CARD, sanction.getType());
        assertEquals(1, sanction.getMatchesRemaining());
    }

    @Test
    void toDocument_convierteTodosLosCampos() {
        UUID id = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        PlayerSanction sanction = PlayerSanction.reconstruct(id, playerId, SanctionType.CONDUCT, 3);

        PlayerSanctionDocument document = mapper.toDocument(sanction);

        assertEquals(id, document.getId());
        assertEquals(playerId, document.getPlayerId());
        assertEquals("CONDUCT", document.getType());
        assertEquals(3, document.getMatchesRemaining());
    }
}
