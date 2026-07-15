package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentPauseNotAllowedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TournamentPauseTest {

    private Tournament sampleTournament(TournamentStatus status) {
        return Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                status
        );
    }

    @Test
    void pause_torneoActivo_quedaPausado() {
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);

        tournament.pause();

        assertTrue(tournament.isPaused());
        assertEquals(TournamentStatus.ACTIVE, tournament.getStatus());
    }

    @Test
    void pause_torneoEnProgreso_quedaPausado() {
        Tournament tournament = sampleTournament(TournamentStatus.IN_PROGRESS);

        tournament.pause();

        assertTrue(tournament.isPaused());
        assertEquals(TournamentStatus.IN_PROGRESS, tournament.getStatus());
    }

    @Test
    void pause_torneoNoActivoNiEnProgreso_lanzaExcepcion() {
        Tournament tournament = sampleTournament(TournamentStatus.FINISHED);

        assertThrows(TournamentPauseNotAllowedException.class, tournament::pause);
    }

    @Test
    void pause_torneoYaPausado_lanzaExcepcion() {
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);
        tournament.pause();

        assertThrows(TournamentPauseNotAllowedException.class, tournament::pause);
    }

    @Test
    void resume_torneoPausado_quedaSinPausar() {
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);
        tournament.pause();

        tournament.resume();

        assertFalse(tournament.isPaused());
    }

    @Test
    void resume_torneoNoPausado_lanzaExcepcion() {
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);

        assertThrows(TournamentPauseNotAllowedException.class, tournament::resume);
    }
}
