// src/test/java/co/edu/escuelaing/techcup/tournament/domain/model/TournamentTest.java
package co.edu.escuelaing.techcup.tournament.domain.model;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDateRangeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentTest {

    @Test
    void create_withValidNormalData_createsTournamentInActiveStatus() {
        Tournament tournament = Tournament.create(
                "Copa Enero", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 20),
                LocalDate.of(2026, 2, 20),
                null, null
        );

        assertEquals("Copa Enero", tournament.getName());
        assertEquals(TournamentStatus.ACTIVE, tournament.getStatus());
    }

    @Test
    void create_withValidLightningData_derivesEndDateFromStartDate() {
        Tournament tournament = Tournament.create(
                "Copa Relámpago", TournamentType.LIGHTNING, TournamentFormat.GROUPS,
                8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1),
                null,
                LocalDate.of(2026, 2, 20),
                LocalTime.of(9, 0), LocalTime.of(18, 0)
        );

        assertEquals(TournamentStatus.ACTIVE, tournament.getStatus());
        assertEquals(LocalDate.of(2026, 3, 1), tournament.getEndDate());
    }

    @Test
    void create_lightningWithoutTimes_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.create(
                        "Copa Relámpago", TournamentType.LIGHTNING, TournamentFormat.GROUPS,
                        8, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 1), null, LocalDate.of(2026, 2, 20),
                        null, null
                ));
    }

    @Test
    void create_lightningWithEndTimeNotAfterStartTime_throwsException() {
        assertThrows(InvalidTournamentDateRangeException.class,
                () -> Tournament.create(
                        "Copa Relámpago", TournamentType.LIGHTNING, TournamentFormat.GROUPS,
                        8, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 1), null, LocalDate.of(2026, 2, 20),
                        LocalTime.of(18, 0), LocalTime.of(9, 0)
                ));
    }

    @Test
    void create_normalWithoutEndDate_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.create(
                        "Copa Enero", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                        8, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 1), null, LocalDate.of(2026, 2, 20),
                        null, null
                ));
    }

    @Test
    void create_withNullType_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.create(
                        "Copa Enero", null, TournamentFormat.BRACKETS,
                        8, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                        null, null
                ));
    }

    @Test
    void create_withNullFormat_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.create(
                        "Copa Enero", TournamentType.NORMAL, null,
                        8, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                        null, null
                ));
    }

    @Test
    void create_whenStartDateIsNotAfterRegistrationDeadline_throwsException() {
        InvalidTournamentDateRangeException exception = assertThrows(
                InvalidTournamentDateRangeException.class,
                () -> Tournament.create(
                        "Copa Enero", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                        8, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 1),
                        LocalDate.of(2026, 3, 20),
                        LocalDate.of(2026, 3, 1), // igual a startDate, no es anterior
                        null, null
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
                        "Copa Enero", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                        8, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 10),
                        LocalDate.of(2026, 3, 5), // antes que startDate
                        LocalDate.of(2026, 2, 20),
                        null, null
                )
        );

        assertEquals("La fecha de fin debe ser posterior o igual a la fecha de inicio",
                exception.getMessage());
    }

    @Test
    void create_withBlankName_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.create(
                        " ", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                        8, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                        null, null
                ));
    }

    @Test
    void create_withFewerThanMinimumTeams_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.create(
                        "Copa Enero", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                        1, BigDecimal.valueOf(50000),
                        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                        null, null
                ));
    }

    @Test
    void create_withNegativeCost_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.create(
                        "Copa Enero", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                        8, BigDecimal.valueOf(-1),
                        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                        null, null
                ));
    }
}
