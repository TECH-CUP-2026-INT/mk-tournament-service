package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record TournamentResponse(
        String id,
        String name,
        TournamentType type,
        TournamentFormat format,
        int numberOfTeams,
        BigDecimal cost,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate registrationDeadline,
        LocalTime matchStartTime,
        LocalTime matchEndTime,
        TournamentStatus status,
        boolean paused,
        boolean active
) {}
