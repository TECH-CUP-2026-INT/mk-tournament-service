package co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TournamentResponse(
        String id,
        String name,
        int numberOfTeams,
        BigDecimal cost,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate registrationDeadline,
        TournamentStatus status
) {}
