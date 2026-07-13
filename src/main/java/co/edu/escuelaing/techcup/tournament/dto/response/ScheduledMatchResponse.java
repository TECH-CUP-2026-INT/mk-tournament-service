package co.edu.escuelaing.techcup.tournament.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduledMatchResponse(
        String id,
        String matchupId,
        String courtId,
        String refereeId,
        LocalDate matchDate,
        LocalTime matchTime
) {}
