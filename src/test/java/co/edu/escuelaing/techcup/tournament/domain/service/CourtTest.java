package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourtTest {

    @Test
    void assignMatch_conIdValido_asignaElPartido() {
        Court court = Court.create(UUID.randomUUID(), CourtSection.CANCHA_1, "Cancha techada");
        UUID matchId = UUID.randomUUID();

        court.assignMatch(matchId);

        assertEquals(matchId, court.getMatchId());
    }

    @Test
    void assignMatch_conIdNulo_lanzaIllegalArgumentException() {
        Court court = Court.create(UUID.randomUUID(), CourtSection.CANCHA_1, "Cancha techada");

        assertThrows(IllegalArgumentException.class, () -> court.assignMatch(null));
    }
}
