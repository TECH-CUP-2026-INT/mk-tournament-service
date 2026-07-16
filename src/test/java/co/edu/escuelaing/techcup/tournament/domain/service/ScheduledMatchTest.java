package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidScheduledMatchDataException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScheduledMatchTest {

    @Test
    void create_conDatosValidos_creaElPartidoAgendado() {
        UUID matchupId = UUID.randomUUID();
        UUID courtId = UUID.randomUUID();
        UUID refereeId = UUID.randomUUID();
        ScheduledMatch scheduledMatch = ScheduledMatch.create(
                matchupId, courtId, refereeId,
                LocalDate.of(2026, 8, 1), LocalTime.of(15, 0));

        assertEquals(matchupId, scheduledMatch.getMatchupId());
        assertEquals(courtId, scheduledMatch.getCourtId());
        assertEquals(refereeId, scheduledMatch.getRefereeId());
        assertEquals(LocalDate.of(2026, 8, 1), scheduledMatch.getMatchDate());
        assertEquals(LocalTime.of(15, 0), scheduledMatch.getMatchTime());
    }

    @Test
    void create_sinMatchupId_lanzaInvalidScheduledMatchDataException() {
        assertThrows(InvalidScheduledMatchDataException.class,
                () -> ScheduledMatch.create(null, UUID.randomUUID(), UUID.randomUUID(),
                        LocalDate.of(2026, 8, 1), LocalTime.of(15, 0)));
    }

    @Test
    void create_sinCourtId_lanzaInvalidScheduledMatchDataException() {
        assertThrows(InvalidScheduledMatchDataException.class,
                () -> ScheduledMatch.create(UUID.randomUUID(), null, UUID.randomUUID(),
                        LocalDate.of(2026, 8, 1), LocalTime.of(15, 0)));
    }

    @Test
    void create_sinRefereeId_lanzaInvalidScheduledMatchDataException() {
        assertThrows(InvalidScheduledMatchDataException.class,
                () -> ScheduledMatch.create(UUID.randomUUID(), UUID.randomUUID(), null,
                        LocalDate.of(2026, 8, 1), LocalTime.of(15, 0)));
    }

    @Test
    void create_sinFecha_lanzaInvalidScheduledMatchDataException() {
        assertThrows(InvalidScheduledMatchDataException.class,
                () -> ScheduledMatch.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        null, LocalTime.of(15, 0)));
    }

    @Test
    void create_sinHora_lanzaInvalidScheduledMatchDataException() {
        assertThrows(InvalidScheduledMatchDataException.class,
                () -> ScheduledMatch.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        LocalDate.of(2026, 8, 1), null));
    }
}
