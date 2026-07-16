package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface ScheduleMatchUseCase {

    ScheduledMatch schedule(ScheduleMatchCommand command);

    record ScheduleMatchCommand(
            UUID matchupId, LocalDate matchDate, LocalTime matchTime, UUID courtId, UUID refereeId) {}
}
