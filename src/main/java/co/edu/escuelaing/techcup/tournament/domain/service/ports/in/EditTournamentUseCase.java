package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface EditTournamentUseCase {

    Tournament edit(EditTournamentCommand command);

    record EditTournamentCommand(
            UUID tournamentId,
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
