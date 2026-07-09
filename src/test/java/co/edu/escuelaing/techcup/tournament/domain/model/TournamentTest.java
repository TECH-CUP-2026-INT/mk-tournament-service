// src/test/java/co/edu/escuelaing/techcup/tournament/domain/model/TournamentTest.java
package co.edu.escuelaing.techcup.tournament.domain.model;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDateRangeException;
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
}