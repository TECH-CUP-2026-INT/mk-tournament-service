package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.PlayerSanctionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlayerSanctionMongoRepository extends MongoRepository<PlayerSanctionDocument, String> {
    List<PlayerSanctionDocument> findByPlayerIdAndMatchesRemainingGreaterThan(String playerId, int threshold);
    List<PlayerSanctionDocument> findByMatchesRemainingGreaterThan(int threshold);
}
