package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

public interface ScheduledMatchMongoRepository extends MongoRepository<ScheduledMatchDocument, UUID> {
    boolean existsByCourtIdAndMatchDateAndMatchTimeOrRefereeIdAndMatchDateAndMatchTime(
            UUID courtId, LocalDate courtMatchDate, LocalTime courtMatchTime,
            UUID refereeId, LocalDate refereeMatchDate, LocalTime refereeMatchTime);

    Optional<ScheduledMatchDocument> findByMatchupId(UUID matchupId);
}
