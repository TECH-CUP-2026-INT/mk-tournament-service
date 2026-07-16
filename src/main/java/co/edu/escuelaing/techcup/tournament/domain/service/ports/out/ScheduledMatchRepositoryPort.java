package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Puerto de persistencia para partidos programados (fecha, cancha, árbitro).
 */
public interface ScheduledMatchRepositoryPort {
    ScheduledMatch save(ScheduledMatch scheduledMatch);

    boolean existsConflict(String courtId, String refereeId, LocalDate matchDate, LocalTime matchTime);

    Optional<ScheduledMatch> findByMatchupId(String matchupId);
}
