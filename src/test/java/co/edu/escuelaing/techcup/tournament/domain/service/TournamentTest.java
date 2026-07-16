package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDateRangeException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentCannotBeFinalizedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentTest {

    @Test
    void create_withValidNormalData_createsTournamentInActiveStatus() {
        Tournament tournament = Tournament.builder()
                .name("Copa Enero").type(TournamentType.NORMAL).format(TournamentFormat.BRACKETS)
                .numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, 3, 1))
                .endDate(LocalDate.of(2026, 3, 20))
                .registrationDeadline(LocalDate.of(2026, 2, 20))
                .create();

        assertEquals("Copa Enero", tournament.getName());
        assertEquals(TournamentStatus.ACTIVE, tournament.getStatus());
    }

    @Test
    void create_withValidLightningData_derivesEndDateFromStartDate() {
        Tournament tournament = Tournament.builder()
                .name("Copa Relámpago").type(TournamentType.LIGHTNING).format(TournamentFormat.GROUPS)
                .numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, 3, 1))
                .registrationDeadline(LocalDate.of(2026, 2, 20))
                .matchStartTime(LocalTime.of(9, 0)).matchEndTime(LocalTime.of(18, 0))
                .create();

        assertEquals(TournamentStatus.ACTIVE, tournament.getStatus());
        assertEquals(LocalDate.of(2026, 3, 1), tournament.getEndDate());
    }

    @Test
    void create_lightningWithoutTimes_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.builder()
                        .name("Copa Relámpago").type(TournamentType.LIGHTNING).format(TournamentFormat.GROUPS)
                        .numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                        .startDate(LocalDate.of(2026, 3, 1))
                        .registrationDeadline(LocalDate.of(2026, 2, 20))
                        .create());
    }

    @Test
    void create_lightningWithEndTimeNotAfterStartTime_throwsException() {
        assertThrows(InvalidTournamentDateRangeException.class,
                () -> Tournament.builder()
                        .name("Copa Relámpago").type(TournamentType.LIGHTNING).format(TournamentFormat.GROUPS)
                        .numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                        .startDate(LocalDate.of(2026, 3, 1))
                        .registrationDeadline(LocalDate.of(2026, 2, 20))
                        .matchStartTime(LocalTime.of(18, 0)).matchEndTime(LocalTime.of(9, 0))
                        .create());
    }

    @Test
    void create_normalWithoutEndDate_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.builder()
                        .name("Copa Enero").type(TournamentType.NORMAL).format(TournamentFormat.BRACKETS)
                        .numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                        .startDate(LocalDate.of(2026, 3, 1))
                        .registrationDeadline(LocalDate.of(2026, 2, 20))
                        .create());
    }

    @Test
    void create_withNullType_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.builder()
                        .name("Copa Enero").type(null).format(TournamentFormat.BRACKETS)
                        .numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                        .startDate(LocalDate.of(2026, 3, 1)).endDate(LocalDate.of(2026, 3, 20))
                        .registrationDeadline(LocalDate.of(2026, 2, 20))
                        .create());
    }

    @Test
    void create_withNullFormat_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.builder()
                        .name("Copa Enero").type(TournamentType.NORMAL).format(null)
                        .numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                        .startDate(LocalDate.of(2026, 3, 1)).endDate(LocalDate.of(2026, 3, 20))
                        .registrationDeadline(LocalDate.of(2026, 2, 20))
                        .create());
    }

    @Test
    void create_whenStartDateIsNotAfterRegistrationDeadline_throwsException() {
        InvalidTournamentDateRangeException exception = assertThrows(
                InvalidTournamentDateRangeException.class,
                () -> Tournament.builder()
                        .name("Copa Enero").type(TournamentType.NORMAL).format(TournamentFormat.BRACKETS)
                        .numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                        .startDate(LocalDate.of(2026, 3, 1))
                        .endDate(LocalDate.of(2026, 3, 20))
                        .registrationDeadline(LocalDate.of(2026, 3, 1)) // igual a startDate, no es anterior
                        .create()
        );

        assertEquals("La fecha de inicio debe ser posterior a la fecha de cierre de inscripciones",
                exception.getMessage());
    }

    @Test
    void create_whenEndDateIsBeforeStartDate_throwsException() {
        InvalidTournamentDateRangeException exception = assertThrows(
                InvalidTournamentDateRangeException.class,
                () -> Tournament.builder()
                        .name("Copa Enero").type(TournamentType.NORMAL).format(TournamentFormat.BRACKETS)
                        .numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                        .startDate(LocalDate.of(2026, 3, 10))
                        .endDate(LocalDate.of(2026, 3, 5)) // antes que startDate
                        .registrationDeadline(LocalDate.of(2026, 2, 20))
                        .create()
        );

        assertEquals("La fecha de fin debe ser posterior o igual a la fecha de inicio",
                exception.getMessage());
    }

    @Test
    void create_withBlankName_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.builder()
                        .name(" ").type(TournamentType.NORMAL).format(TournamentFormat.BRACKETS)
                        .numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                        .startDate(LocalDate.of(2026, 3, 1)).endDate(LocalDate.of(2026, 3, 20))
                        .registrationDeadline(LocalDate.of(2026, 2, 20))
                        .create());
    }

    @Test
    void create_withFewerThanMinimumTeams_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.builder()
                        .name("Copa Enero").type(TournamentType.NORMAL).format(TournamentFormat.BRACKETS)
                        .numberOfTeams(1).cost(BigDecimal.valueOf(50000))
                        .startDate(LocalDate.of(2026, 3, 1)).endDate(LocalDate.of(2026, 3, 20))
                        .registrationDeadline(LocalDate.of(2026, 2, 20))
                        .create());
    }

    @Test
    void create_withNegativeCost_throwsException() {
        assertThrows(InvalidTournamentDataException.class,
                () -> Tournament.builder()
                        .name("Copa Enero").type(TournamentType.NORMAL).format(TournamentFormat.BRACKETS)
                        .numberOfTeams(8).cost(BigDecimal.valueOf(-1))
                        .startDate(LocalDate.of(2026, 3, 1)).endDate(LocalDate.of(2026, 3, 20))
                        .registrationDeadline(LocalDate.of(2026, 2, 20))
                        .create());
    }

    @Test
    void finish_whenInProgressAndEndDateReached_setsStatusFinished() {
        Tournament tournament = Tournament.builder()
                .id(UUID.randomUUID()).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, 3, 1)).endDate(LocalDate.of(2026, 3, 10))
                .registrationDeadline(LocalDate.of(2026, 2, 20))
                .status(TournamentStatus.IN_PROGRESS)
                .reconstruct();

        tournament.finish(LocalDate.of(2026, 3, 10));

        assertEquals(TournamentStatus.FINISHED, tournament.getStatus());
    }

    @Test
    void finish_whenNotInProgress_throwsException() {
        Tournament tournament = Tournament.builder()
                .id(UUID.randomUUID()).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, 3, 1)).endDate(LocalDate.of(2026, 3, 10))
                .registrationDeadline(LocalDate.of(2026, 2, 20))
                .status(TournamentStatus.DRAFT)
                .reconstruct();

        TournamentCannotBeFinalizedException exception = assertThrows(
                TournamentCannotBeFinalizedException.class,
                () -> tournament.finish(LocalDate.of(2026, 3, 10))
        );

        assertEquals("El torneo debe estar En Progreso para poder finalizarse", exception.getMessage());
    }

    @Test
    void finish_whenEndDateNotReached_throwsException() {
        Tournament tournament = Tournament.builder()
                .id(UUID.randomUUID()).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, 3, 1)).endDate(LocalDate.of(2026, 3, 20))
                .registrationDeadline(LocalDate.of(2026, 2, 20))
                .status(TournamentStatus.IN_PROGRESS)
                .reconstruct();

        TournamentCannotBeFinalizedException exception = assertThrows(
                TournamentCannotBeFinalizedException.class,
                () -> tournament.finish(LocalDate.of(2026, 3, 10)) // "hoy" es antes de endDate
        );

        assertEquals("La fecha de fin no ha sido alcanzada", exception.getMessage());
    }

    @Test
    void finish_whenAlreadyFinished_throwsException() {
        Tournament tournament = Tournament.builder()
                .id(UUID.randomUUID()).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, 3, 1)).endDate(LocalDate.of(2026, 3, 10))
                .registrationDeadline(LocalDate.of(2026, 2, 20))
                .status(TournamentStatus.FINISHED)
                .reconstruct();

        assertThrows(TournamentCannotBeFinalizedException.class,
                () -> tournament.finish(LocalDate.of(2026, 3, 10)));
    }
}
