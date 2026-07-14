package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = """
        Request to edit an existing tournament. Every field is optional — send only the ones you \
        want to change; omitted fields (null) keep their current value. Editing is blocked once the \
        tournament is FINISHED.""")
public record EditTournamentRequest(
        @Schema(description = "New tournament name.", example = "TechCup Football 2026", maxLength = 100)
        @Size(max = 100) String name,

        @Schema(description = "New tournament type.", example = "NORMAL")
        TournamentType type,

        @Schema(description = "New bracket format.", example = "BRACKETS")
        TournamentFormat format,

        @Schema(description = "New number of participating teams.", example = "8")
        Integer numberOfTeams,

        @Schema(description = "New enrollment fee, in Colombian pesos (COP).", example = "50000.00", minimum = "0")
        @DecimalMin(value = "0") BigDecimal cost,

        @Schema(description = "New enrollment deadline.", example = "2026-07-25")
        LocalDate registrationDeadline,

        @Schema(description = "New tournament start date.", example = "2026-08-01")
        LocalDate startDate,

        @Schema(description = "New tournament end date (NORMAL tournaments only).", example = "2026-08-31")
        LocalDate endDate,

        @Schema(description = "New match start time (LIGHTNING tournaments only).", example = "09:00")
        LocalTime matchStartTime,

        @Schema(description = "New match end time (LIGHTNING tournaments only).", example = "21:00")
        LocalTime matchEndTime
) {}
