package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface PlayerSanctionMongoRepository extends MongoRepository<PlayerSanctionDocument, UUID> {
    List<PlayerSanctionDocument> findByPlayerIdAndMatchesRemainingGreaterThan(UUID playerId, int threshold);
    List<PlayerSanctionDocument> findByMatchesRemainingGreaterThan(int threshold);
}
