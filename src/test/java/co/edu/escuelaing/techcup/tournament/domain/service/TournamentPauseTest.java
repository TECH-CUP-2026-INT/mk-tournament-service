package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentPauseNotAllowedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TournamentPauseTest {

    private Tournament sampleTournament(TournamentStatus status) {
        return Tournament.builder()
                .id(UUID.randomUUID()).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(status)
                .reconstruct();
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
