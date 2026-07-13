package co.edu.escuelaing.techcup.tournament.service;

import co.edu.escuelaing.techcup.tournament.exception.InvalidScheduledMatchDataException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScheduledMatchTest {

    @Test
    void create_conDatosValidos_creaElPartidoAgendado() {
        ScheduledMatch scheduledMatch = ScheduledMatch.create(
                "matchup-1", "court-1", "referee-1",
                LocalDate.of(2026, 8, 1), LocalTime.of(15, 0));

        assertEquals("matchup-1", scheduledMatch.getMatchupId());
        assertEquals("court-1", scheduledMatch.getCourtId());
        assertEquals("referee-1", scheduledMatch.getRefereeId());
        assertEquals(LocalDate.of(2026, 8, 1), scheduledMatch.getMatchDate());
        assertEquals(LocalTime.of(15, 0), scheduledMatch.getMatchTime());
    }

    @Test
    void create_sinMatchupId_lanzaInvalidScheduledMatchDataException() {
        assertThrows(InvalidScheduledMatchDataException.class,
                () -> ScheduledMatch.create(null, "court-1", "referee-1",
                        LocalDate.of(2026, 8, 1), LocalTime.of(15, 0)));
    }

    @Test
    void create_sinCourtId_lanzaInvalidScheduledMatchDataException() {
        assertThrows(InvalidScheduledMatchDataException.class,
                () -> ScheduledMatch.create("matchup-1", " ", "referee-1",
                        LocalDate.of(2026, 8, 1), LocalTime.of(15, 0)));
    }

    @Test
    void create_sinRefereeId_lanzaInvalidScheduledMatchDataException() {
        assertThrows(InvalidScheduledMatchDataException.class,
                () -> ScheduledMatch.create("matchup-1", "court-1", null,
                        LocalDate.of(2026, 8, 1), LocalTime.of(15, 0)));
    }

    @Test
    void create_sinFecha_lanzaInvalidScheduledMatchDataException() {
        assertThrows(InvalidScheduledMatchDataException.class,
                () -> ScheduledMatch.create("matchup-1", "court-1", "referee-1",
                        null, LocalTime.of(15, 0)));
    }

    @Test
    void create_sinHora_lanzaInvalidScheduledMatchDataException() {
        assertThrows(InvalidScheduledMatchDataException.class,
                () -> ScheduledMatch.create("matchup-1", "court-1", "referee-1",
                        LocalDate.of(2026, 8, 1), null));
    }
}
