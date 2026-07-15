package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;

import co.edu.escuelaing.techcup.tournament.domain.exception.MatchActivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchInactiveException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchActivationTest {

    @Test
    void nuevoPartido_porDefectoEstaActivo() {
        Match match = new Match("m1", "home", "away", MatchStatus.PENDING);

        assertTrue(match.isActive());
    }

    @Test
    void inactivate_partidoActivo_quedaInactivo() {
        Match match = new Match("m1", "home", "away", MatchStatus.PENDING);

        match.inactivate();

        assertFalse(match.isActive());
    }

    @Test
    void inactivate_partidoYaInactivo_lanzaExcepcion() {
        Match match = new Match("m1", "home", "away", MatchStatus.PENDING);
        match.inactivate();

        assertThrows(MatchActivationNotAllowedException.class, match::inactivate);
    }

    @Test
    void reactivate_partidoInactivo_quedaActivo() {
        Match match = new Match("m1", "home", "away", MatchStatus.PENDING);
        match.inactivate();

        match.reactivate();

        assertTrue(match.isActive());
    }

    @Test
    void reactivate_partidoYaActivo_lanzaExcepcion() {
        Match match = new Match("m1", "home", "away", MatchStatus.PENDING);

        assertThrows(MatchActivationNotAllowedException.class, match::reactivate);
    }

    @Test
    void finish_partidoInactivo_lanzaMatchInactiveException() {
        Match match = new Match("m1", "home", "away", MatchStatus.PENDING,
                true, 0, 0, null);
        match.inactivate();

        assertThrows(MatchInactiveException.class, () -> match.finish(2, 1));
    }

    @Test
    void recordPenaltyShootoutWinner_partidoInactivo_lanzaMatchInactiveException() {
        Match match = new Match("m1", "home", "away", MatchStatus.FINISHED,
                true, 1, 1, null);
        match.inactivate();

        assertThrows(MatchInactiveException.class, () -> match.recordPenaltyShootoutWinner("home"));
    }

    @Test
    void markAsNoShow_partidoInactivo_lanzaMatchInactiveException() {
        Match match = new Match("m1", "home", "away", MatchStatus.PENDING);
        match.inactivate();

        assertThrows(MatchInactiveException.class, match::markAsNoShow);
    }

    @Test
    void inactivate_preservaDatosYaRegistrados() {
        Match match = new Match("m1", "home", "away", MatchStatus.FINISHED,
                true, 3, 1, null);

        match.inactivate();

        assertEquals(3, match.getHomeScore());
        assertEquals(1, match.getAwayScore());
        assertEquals(MatchStatus.FINISHED, match.getStatus());
    }
}
