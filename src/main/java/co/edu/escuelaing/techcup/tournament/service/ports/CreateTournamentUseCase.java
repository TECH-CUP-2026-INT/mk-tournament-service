package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public interface CreateTournamentUseCase {
    Tournament create(CreateTournamentCommand command);

    record CreateTournamentCommand(
            String name,
            TournamentType type,
            TournamentFormat format,
            int numberOfTeams,
            BigDecimal cost,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate registrationDeadline,
            LocalTime matchStartTime,
            LocalTime matchEndTime
    ) {}
}
