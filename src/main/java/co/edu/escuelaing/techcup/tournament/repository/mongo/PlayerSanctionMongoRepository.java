package co.edu.escuelaing.techcup.tournament.repository.mongo;

import co.edu.escuelaing.techcup.tournament.entity.document.PlayerSanctionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlayerSanctionMongoRepository extends MongoRepository<PlayerSanctionDocument, String> {
    List<PlayerSanctionDocument> findByPlayerIdAndMatchesRemainingGreaterThan(String playerId, int threshold);
    List<PlayerSanctionDocument> findByMatchesRemainingGreaterThan(int threshold);
}
