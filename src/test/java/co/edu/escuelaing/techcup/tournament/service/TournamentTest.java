package co.edu.escuelaing.techcup.tournament.service;

import co.edu.escuelaing.techcup.tournament.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.exception.InvalidTournamentDateRangeException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentCannotBeFinalizedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentTest {

    @Test
    void create_withValidData_createsTournamentInDraftStatus() {
        Tournament tournament = Tournament.create(
                "Copa Enero",
                8,
                BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 20),
                LocalDate.of(2026, 2, 20)
        );

        assertEquals("Copa Enero", tournament.getName());
        assertEquals(TournamentStatus.DRAFT, tournament.getStatus());
    }

    @Test
    void create_whenStartDateIsNotAfterRegistrationDeadline_throwsException() {
        InvalidTournamentDateRangeException exception = assertThrows(
                InvalidTournamentDateRangeException.class,
                () -> Tournament.create(
                        "Copa Enero", 8, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 1),
                        LocalDate.of(2026, 3, 20),
                        LocalDate.of(2026, 3, 1) // igual a startDate, no es anterior
                )
        );

        assertEquals("La fecha de inicio debe ser posterior a la fecha de cierre de inscripciones",
                exception.getMessage());
    }

    @Test
    void create_whenEndDateIsBeforeStartDate_throwsException() {
        InvalidTournamentDateRangeException exception = assertThrows(
                InvalidTournamentDateRangeException.class,
                () -> Tournament.create(
                        "Copa Enero", 8, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 10),
                        LocalDate.of(2026, 3, 5), // antes que startDate
                        LocalDate.of(2026, 2, 20)
                )
        );

        assertEquals("La fecha de fin debe ser posterior o igual a la fecha de inicio",
                exception.getMessage());
    }

    @Test
    void create_withBlankName_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.create(
                        " ", 8, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20)
                ));
    }

    @Test
    void create_withFewerThanMinimumTeams_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.create(
                        "Copa Enero", 1, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20)
                ));
    }

    @Test
    void create_withNegativeCost_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.create(
                        "Copa Enero", 8, BigDecimal.valueOf(-1),
                        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20)
                ));
    }

    @Test
    void finish_whenInProgressAndEndDateReached_setsStatusFinished() {
        Tournament tournament = Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), LocalDate.of(2026, 2, 20),
                TournamentStatus.IN_PROGRESS
        );

        tournament.finish(LocalDate.of(2026, 3, 10));

        assertEquals(TournamentStatus.FINISHED, tournament.getStatus());
    }

    @Test
    void finish_whenNotInProgress_throwsException() {
        Tournament tournament = Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), LocalDate.of(2026, 2, 20),
                TournamentStatus.DRAFT
        );

        TournamentCannotBeFinalizedException exception = assertThrows(
                TournamentCannotBeFinalizedException.class,
                () -> tournament.finish(LocalDate.of(2026, 3, 10))
        );

        assertEquals("El torneo debe estar En Progreso para poder finalizarse", exception.getMessage());
    }

    @Test
    void finish_whenEndDateNotReached_throwsException() {
        Tournament tournament = Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                TournamentStatus.IN_PROGRESS
        );

        TournamentCannotBeFinalizedException exception = assertThrows(
                TournamentCannotBeFinalizedException.class,
                () -> tournament.finish(LocalDate.of(2026, 3, 10)) // "hoy" es antes de endDate
        );

        assertEquals("La fecha de fin no ha sido alcanzada", exception.getMessage());
    }

    @Test
    void finish_whenAlreadyFinished_throwsException() {
        Tournament tournament = Tournament.reconstruct(
                "1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), LocalDate.of(2026, 2, 20),
                TournamentStatus.FINISHED
        );

        assertThrows(TournamentCannotBeFinalizedException.class,
                () -> tournament.finish(LocalDate.of(2026, 3, 10)));
    }
}