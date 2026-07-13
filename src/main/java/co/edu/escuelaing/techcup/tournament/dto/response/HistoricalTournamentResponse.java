package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HistoricalTournamentResponse(
        String id,
        String name,
        int numberOfTeams,
        BigDecimal cost,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate registrationDeadline,
        TournamentStatus status,
        String championTeamId
) {}
