package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.PlayerSanctionDocument;
import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerSanctionPersistenceMapperTest {

    private final PlayerSanctionPersistenceMapper mapper = Mappers.getMapper(PlayerSanctionPersistenceMapper.class);

    @Test
    void toDomain_convierteTodosLosCampos() {
        PlayerSanctionDocument document = new PlayerSanctionDocument("s1", "player1", "RED_CARD", 1);

        PlayerSanction sanction = mapper.toDomain(document);

        assertEquals("s1", sanction.getId());
        assertEquals("player1", sanction.getPlayerId());
        assertEquals(SanctionType.RED_CARD, sanction.getType());
        assertEquals(1, sanction.getMatchesRemaining());
    }

    @Test
    void toDocument_convierteTodosLosCampos() {
        PlayerSanction sanction = PlayerSanction.reconstruct("s1", "player1", SanctionType.CONDUCT, 3);

        PlayerSanctionDocument document = mapper.toDocument(sanction);

        assertEquals("s1", document.getId());
        assertEquals("player1", document.getPlayerId());
        assertEquals("CONDUCT", document.getType());
        assertEquals(3, document.getMatchesRemaining());
    }
}
