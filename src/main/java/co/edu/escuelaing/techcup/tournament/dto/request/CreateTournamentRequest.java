package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = """
        Request to create a tournament. The tournament is created directly in ACTIVE status \
        (there is no Draft step). For a NORMAL tournament, provide startDate/endDate and leave \
        matchStartTime/matchEndTime null. For a LIGHTNING tournament, provide matchStartTime/matchEndTime \
        instead — endDate is derived automatically from startDate.""")
public record CreateTournamentRequest(

        @Schema(description = "Tournament name, shown to all users.", example = "TechCup Football 2026", maxLength = 100)
        @NotBlank(message = "El nombre del torneo es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String name,

        @Schema(description = "Tournament type. NORMAL runs over a date range; LIGHTNING runs in a single day.",
                example = "NORMAL")
        @NotNull(message = "El tipo de torneo es obligatorio")
        TournamentType type,

        @Schema(description = """
                Bracket format used to generate the fixture: BRACKETS (single-elimination), \
                GROUPS (group stage + cross), or LEAGUE (round robin).""", example = "BRACKETS")
        @NotNull(message = "El formato del torneo es obligatorio")
        TournamentFormat format,

        @Schema(description = "Number of participating teams (minimum 3).", example = "8", minimum = "3")
        @Min(value = 3, message = "La cantidad de equipos debe ser mayor o igual a 3")
        int numberOfTeams,

        @Schema(description = "Enrollment fee, in Colombian pesos (COP).", example = "50000.00", minimum = "0")
        @NotNull(message = "El costo de inscripción es obligatorio")
        @DecimalMin(value = "0", message = "El costo de inscripción no puede ser negativo")
        BigDecimal cost,

        @Schema(description = "Tournament start date. For LIGHTNING tournaments this is the single day it runs on.",
                example = "2026-08-01")
        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate startDate,

        @Schema(description = """
                Tournament end date. Only used for NORMAL tournaments — for LIGHTNING tournaments this \
                is auto-derived to equal startDate and any value sent here is ignored.""", example = "2026-08-31")
        LocalDate endDate,

        @Schema(description = "Deadline for teams to complete enrollment; must be before startDate.",
                example = "2026-07-25")
        @NotNull(message = "La fecha de cierre de inscripciones es obligatoria")
        LocalDate registrationDeadline,

        @Schema(description = "Match start time. Required for LIGHTNING tournaments, ignored for NORMAL ones.",
                example = "09:00")
        LocalTime matchStartTime,

        @Schema(description = "Match end time. Required for LIGHTNING tournaments, ignored for NORMAL ones.",
                example = "21:00")
        LocalTime matchEndTime
) {}
