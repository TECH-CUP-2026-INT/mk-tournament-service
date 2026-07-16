package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.ScheduledMatchDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface ScheduledMatchMongoRepository extends MongoRepository<ScheduledMatchDocument, String> {
    boolean existsByCourtIdAndMatchDateAndMatchTimeOrRefereeIdAndMatchDateAndMatchTime(
            String courtId, LocalDate courtMatchDate, LocalTime courtMatchTime,
            String refereeId, LocalDate refereeMatchDate, LocalTime refereeMatchTime);

    Optional<ScheduledMatchDocument> findByMatchupId(String matchupId);
}
