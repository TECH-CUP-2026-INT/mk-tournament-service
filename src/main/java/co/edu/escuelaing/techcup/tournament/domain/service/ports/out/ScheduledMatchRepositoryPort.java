package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para partidos programados (fecha, cancha, árbitro).
 */
public interface ScheduledMatchRepositoryPort {
    ScheduledMatch save(ScheduledMatch scheduledMatch);

    boolean existsConflict(UUID courtId, UUID refereeId, LocalDate matchDate, LocalTime matchTime);

    Optional<ScheduledMatch> findByMatchupId(UUID matchupId);
}
