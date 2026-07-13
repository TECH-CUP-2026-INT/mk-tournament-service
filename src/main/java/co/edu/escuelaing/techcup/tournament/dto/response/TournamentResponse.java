package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;

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
        TournamentStatus status,
        TournamentType tournamentType,
        TournamentFormat tournamentFormat
) {}
