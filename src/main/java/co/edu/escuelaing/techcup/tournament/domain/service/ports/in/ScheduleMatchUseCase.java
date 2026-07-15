package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ScheduleMatchUseCase {

    ScheduledMatch schedule(ScheduleMatchCommand command);

    record ScheduleMatchCommand(
            String matchupId, LocalDate matchDate, LocalTime matchTime, String courtId, String refereeId) {}
}
