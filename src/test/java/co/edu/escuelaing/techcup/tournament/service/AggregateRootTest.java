package co.edu.escuelaing.techcup.tournament.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AggregateRootTest {

    @Test
    void equals_mismaInstancia_esTrue() {
        Court court = Court.reconstruct("c1", "t1", CourtSection.CANCHA_1, null, null, null);

        assertTrue(court.equals(court));
    }

    @Test
    void equals_conNull_esFalse() {
        Court court = Court.reconstruct("c1", "t1", CourtSection.CANCHA_1, null, null, null);

        assertFalse(court.equals(null));
    }

    @Test
    void equals_conClaseDistinta_esFalse() {
        Court court = Court.reconstruct("c1", "t1", CourtSection.CANCHA_1, null, null, null);
        TournamentParticipant participant = TournamentParticipant.reconstruct("c1", "t1", "user1", ParticipantStatus.ACTIVE);

        assertNotEquals(court, participant);
    }

    @Test
    void equals_conMismoId_esTrue() {
        Court court1 = Court.reconstruct("c1", "t1", CourtSection.CANCHA_1, null, null, null);
        Court court2 = Court.reconstruct("c1", "t2", CourtSection.CANCHA_2, "otra descripción", null, null);

        assertEquals(court1, court2);
        assertEquals(court1.hashCode(), court2.hashCode());
    }

    @Test
    void equals_conIdDistinto_esFalse() {
        Court court1 = Court.reconstruct("c1", "t1", CourtSection.CANCHA_1, null, null, null);
        Court court2 = Court.reconstruct("c2", "t1", CourtSection.CANCHA_1, null, null, null);

        assertNotEquals(court1, court2);
    }

    @Test
    void equals_conIdNulo_esFalse() {
        Court court1 = Court.reconstruct(null, "t1", CourtSection.CANCHA_1, null, null, null);
        Court court2 = Court.reconstruct(null, "t1", CourtSection.CANCHA_1, null, null, null);

        assertNotEquals(court1, court2);
    }

    @Test
    void hashCode_conIdNulo_noLanzaExcepcion() {
        Court court = Court.reconstruct(null, "t1", CourtSection.CANCHA_1, null, null, null);

        assertEquals(0, court.hashCode());
    }
}
