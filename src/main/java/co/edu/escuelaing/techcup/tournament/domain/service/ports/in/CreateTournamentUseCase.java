package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;

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
