package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface EditTournamentUseCase {

    Tournament edit(EditTournamentCommand command);

    record EditTournamentCommand(
            String tournamentId,
            String name,
            TournamentType tournamentType,
            TournamentFormat tournamentFormat,
            Integer numberOfTeams,
            BigDecimal cost,
            LocalDate registrationDeadline,
            LocalDate startDate,
            LocalDate endDate
    ) {}
}
