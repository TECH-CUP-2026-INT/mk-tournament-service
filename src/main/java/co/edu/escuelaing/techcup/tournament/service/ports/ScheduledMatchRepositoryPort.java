package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.ScheduledMatch;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ScheduledMatchRepositoryPort {
    ScheduledMatch save(ScheduledMatch scheduledMatch);

    boolean existsConflict(String courtId, String refereeId, LocalDate matchDate, LocalTime matchTime);
}
