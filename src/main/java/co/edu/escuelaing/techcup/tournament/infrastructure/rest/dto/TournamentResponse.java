package co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;

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
        TournamentStatus status
) {}
