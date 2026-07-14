package co.edu.escuelaing.techcup.tournament.repository.mongo;

import co.edu.escuelaing.techcup.tournament.entity.document.CourtDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CourtMongoRepository extends MongoRepository<CourtDocument, String> {
    Optional<CourtDocument> findByMatchId(String matchId);
}
