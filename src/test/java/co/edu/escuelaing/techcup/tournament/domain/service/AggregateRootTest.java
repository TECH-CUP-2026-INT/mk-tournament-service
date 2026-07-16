package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import co.edu.escuelaing.techcup.tournament.domain.model.ParticipantStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentParticipant;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AggregateRootTest {

    @Test
    void equals_mismaInstancia_esTrue() {
        Court court = Court.reconstruct(UUID.randomUUID(), UUID.randomUUID(), CourtSection.CANCHA_1, null, null, null);

        assertEquals(court, court);
    }

    @Test
    void equals_conNull_esFalse() {
        Court court = Court.reconstruct(UUID.randomUUID(), UUID.randomUUID(), CourtSection.CANCHA_1, null, null, null);

        assertNotEquals(court, null);
    }

    @Test
    void equals_conClaseDistinta_esFalse() {
        UUID id = UUID.randomUUID();
        Court court = Court.reconstruct(id, UUID.randomUUID(), CourtSection.CANCHA_1, null, null, null);
        TournamentParticipant participant = TournamentParticipant.reconstruct(id, UUID.randomUUID(), UUID.randomUUID(), ParticipantStatus.ACTIVE);

        assertNotEquals(court, participant);
    }

    @Test
    void equals_conMismoId_esTrue() {
        UUID id = UUID.randomUUID();
        Court court1 = Court.reconstruct(id, UUID.randomUUID(), CourtSection.CANCHA_1, null, null, null);
        Court court2 = Court.reconstruct(id, UUID.randomUUID(), CourtSection.CANCHA_2, "otra descripción", null, null);

        assertEquals(court1, court2);
        assertEquals(court1.hashCode(), court2.hashCode());
    }

    @Test
    void equals_conIdDistinto_esFalse() {
        UUID tournamentId = UUID.randomUUID();
        Court court1 = Court.reconstruct(UUID.randomUUID(), tournamentId, CourtSection.CANCHA_1, null, null, null);
        Court court2 = Court.reconstruct(UUID.randomUUID(), tournamentId, CourtSection.CANCHA_1, null, null, null);

        assertNotEquals(court1, court2);
    }

    @Test
    void equals_conIdNulo_esFalse() {
        UUID tournamentId = UUID.randomUUID();
        Court court1 = Court.reconstruct(null, tournamentId, CourtSection.CANCHA_1, null, null, null);
        Court court2 = Court.reconstruct(null, tournamentId, CourtSection.CANCHA_1, null, null, null);

        assertNotEquals(court1, court2);
    }

    @Test
    void hashCode_conIdNulo_noLanzaExcepcion() {
        Court court = Court.reconstruct(null, UUID.randomUUID(), CourtSection.CANCHA_1, null, null, null);

        assertEquals(0, court.hashCode());
    }
}
