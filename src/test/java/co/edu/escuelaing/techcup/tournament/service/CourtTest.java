package co.edu.escuelaing.techcup.tournament.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourtTest {

    @Test
    void assignMatch_conIdValido_asignaElPartido() {
        Court court = Court.create("t1", CourtSection.CANCHA_1, "Cancha techada");

        court.assignMatch("m1");

        assertEquals("m1", court.getMatchId());
    }

    @Test
    void assignMatch_conIdVacio_lanzaIllegalArgumentException() {
        Court court = Court.create("t1", CourtSection.CANCHA_1, "Cancha techada");

        assertThrows(IllegalArgumentException.class, () -> court.assignMatch(" "));
    }
}
