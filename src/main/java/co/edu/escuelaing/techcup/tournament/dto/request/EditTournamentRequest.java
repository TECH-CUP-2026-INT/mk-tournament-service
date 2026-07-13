package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record EditTournamentRequest(
        @Size(max = 100) String name,
        TournamentType type,
        TournamentFormat format,
        Integer numberOfTeams,
        @DecimalMin(value = "0") BigDecimal cost,
        LocalDate registrationDeadline,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime matchStartTime,
        LocalTime matchEndTime
) {}
