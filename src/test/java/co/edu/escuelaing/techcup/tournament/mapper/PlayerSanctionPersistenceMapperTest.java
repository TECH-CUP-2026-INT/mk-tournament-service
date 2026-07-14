package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.entity.document.PlayerSanctionDocument;
import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.service.SanctionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerSanctionPersistenceMapperTest {

    @Test
    void toDomain_convierteTodosLosCampos() {
        PlayerSanctionDocument document = new PlayerSanctionDocument("s1", "player1", "RED_CARD", 1);

        PlayerSanction sanction = PlayerSanctionPersistenceMapper.toDomain(document);

        assertEquals("s1", sanction.getId());
        assertEquals("player1", sanction.getPlayerId());
        assertEquals(SanctionType.RED_CARD, sanction.getType());
        assertEquals(1, sanction.getMatchesRemaining());
    }

    @Test
    void toDocument_convierteTodosLosCampos() {
        PlayerSanction sanction = PlayerSanction.reconstruct("s1", "player1", SanctionType.CONDUCT, 3);

        PlayerSanctionDocument document = PlayerSanctionPersistenceMapper.toDocument(sanction);

        assertEquals("s1", document.getId());
        assertEquals("player1", document.getPlayerId());
        assertEquals("CONDUCT", document.getType());
        assertEquals(3, document.getMatchesRemaining());
    }
}
