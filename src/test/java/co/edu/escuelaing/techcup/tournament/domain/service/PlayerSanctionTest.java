package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidSanctionDataException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerSanctionTest {

    @Test
    void create_redCard_siempreUnPartidoIgnorandoMatchesSuspended() {
        PlayerSanction sanction = PlayerSanction.create(UUID.randomUUID(), SanctionType.RED_CARD, 5);

        assertEquals(1, sanction.getMatchesRemaining());
        assertTrue(sanction.isActive());
    }

    @Test
    void create_yellowCardAccumulation_siempreUnPartido() {
        PlayerSanction sanction = PlayerSanction.create(UUID.randomUUID(), SanctionType.YELLOW_CARD_ACCUMULATION, null);

        assertEquals(1, sanction.getMatchesRemaining());
    }

    @Test
    void create_conduct_conMatchesSuspendedValido_usaEseNumero() {
        PlayerSanction sanction = PlayerSanction.create(UUID.randomUUID(), SanctionType.CONDUCT, 3);

        assertEquals(3, sanction.getMatchesRemaining());
    }

    @Test
    void create_conduct_sinMatchesSuspended_lanzaInvalidSanctionDataException() {
        assertThrows(InvalidSanctionDataException.class,
                () -> PlayerSanction.create(UUID.randomUUID(), SanctionType.CONDUCT, null));
    }

    @Test
    void create_conduct_conMatchesSuspendedCero_lanzaInvalidSanctionDataException() {
        assertThrows(InvalidSanctionDataException.class,
                () -> PlayerSanction.create(UUID.randomUUID(), SanctionType.CONDUCT, 0));
    }

    @Test
    void create_sinPlayerId_lanzaInvalidSanctionDataException() {
        assertThrows(InvalidSanctionDataException.class,
                () -> PlayerSanction.create(null, SanctionType.RED_CARD, null));
    }

    @Test
    void create_sinType_lanzaInvalidSanctionDataException() {
        assertThrows(InvalidSanctionDataException.class,
                () -> PlayerSanction.create(UUID.randomUUID(), null, null));
    }

    @Test
    void serveMatch_decrementaYNoBajaDeCero() {
        PlayerSanction sanction = PlayerSanction.create(UUID.randomUUID(), SanctionType.RED_CARD, null);

        sanction.serveMatch();
        assertEquals(0, sanction.getMatchesRemaining());
        assertFalse(sanction.isActive());

        sanction.serveMatch();
        assertEquals(0, sanction.getMatchesRemaining());
    }
}
