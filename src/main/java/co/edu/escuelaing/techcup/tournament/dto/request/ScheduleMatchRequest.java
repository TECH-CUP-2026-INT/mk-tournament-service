package co.edu.escuelaing.techcup.tournament.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleMatchRequest(

        @NotBlank(message = "El id de la matchup pairing es obligatorio")
        String matchupId,

        @NotNull(message = "La fecha del partido es obligatoria")
        LocalDate matchDate,

        @NotNull(message = "La hora del partido es obligatoria")
        LocalTime matchTime,

        @NotBlank(message = "El id de la cancha es obligatorio")
        String courtId,

        @NotBlank(message = "El id del árbitro es obligatorio")
        String refereeId
) {}
