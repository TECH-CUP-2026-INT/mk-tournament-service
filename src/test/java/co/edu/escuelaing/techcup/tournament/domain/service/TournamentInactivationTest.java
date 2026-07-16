package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentInactiveException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TournamentInactivationTest {

    private Tournament sampleTournament(TournamentStatus status) {
        return Tournament.reconstruct(
                UUID.randomUUID(), "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                status
        );
    }

    @Test
    void inactivate_torneoActivo_quedaInactivo() {
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);

        tournament.inactivate();

        assertFalse(tournament.isActive());
        assertEquals(TournamentStatus.ACTIVE, tournament.getStatus());
    }

    @Test
    void inactivate_torneoEnProgreso_quedaInactivo() {
        Tournament tournament = sampleTournament(TournamentStatus.IN_PROGRESS);

        tournament.inactivate();

        assertFalse(tournament.isActive());
    }

    @Test
    void inactivate_torneoNoActivoNiEnProgreso_lanzaExcepcion() {
        Tournament tournament = sampleTournament(TournamentStatus.FINISHED);

        assertThrows(TournamentInactivationNotAllowedException.class, tournament::inactivate);
    }

    @Test
    void inactivate_torneoYaInactivo_lanzaExcepcion() {
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);
        tournament.inactivate();

        assertThrows(TournamentInactivationNotAllowedException.class, tournament::inactivate);
    }

    @Test
    void reactivate_torneoInactivo_quedaActivo() {
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);
        tournament.inactivate();

        tournament.reactivate();

        assertTrue(tournament.isActive());
    }

    @Test
    void reactivate_torneoNoInactivo_lanzaExcepcion() {
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);

        assertThrows(TournamentInactivationNotAllowedException.class, tournament::reactivate);
    }

    @Test
    void torneoInactivo_bloqueaEdicion() {
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);
        tournament.inactivate();

        assertThrows(TournamentInactiveException.class,
                () -> tournament.update(null, null, null, null, null, null, null, null, null, null));
    }

    @Test
    void torneoInactivo_bloqueaPausar() {
        Tournament tournament = sampleTournament(TournamentStatus.ACTIVE);
        tournament.inactivate();

        assertThrows(TournamentInactiveException.class, tournament::pause);
    }

    @Test
    void torneoInactivo_bloqueaFinalizar() {
        Tournament tournament = sampleTournament(TournamentStatus.IN_PROGRESS);
        tournament.inactivate();

        assertThrows(TournamentInactiveException.class, () -> tournament.finish(LocalDate.of(2026, 3, 25)));
    }
}
