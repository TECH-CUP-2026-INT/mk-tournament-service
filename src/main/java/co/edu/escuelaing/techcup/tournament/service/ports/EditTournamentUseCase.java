package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public interface EditTournamentUseCase {

    Tournament edit(EditTournamentCommand command);

    record EditTournamentCommand(
            String tournamentId,
            String name,
            TournamentType type,
            TournamentFormat format,
            Integer numberOfTeams,
            BigDecimal cost,
            LocalDate registrationDeadline,
            LocalDate startDate,
            LocalDate endDate,
            LocalTime matchStartTime,
            LocalTime matchEndTime
    ) {}
}
