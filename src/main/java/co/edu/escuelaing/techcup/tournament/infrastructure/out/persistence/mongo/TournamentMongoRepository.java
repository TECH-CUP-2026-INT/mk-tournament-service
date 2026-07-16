package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TournamentMongoRepository extends MongoRepository<TournamentDocument, UUID> {
    List<TournamentDocument> findAllByStatus(String status);
    Optional<TournamentDocument> findByIdAndStatus(UUID id, String status);
    Optional<TournamentDocument> findByMatchesMatchId(UUID matchId);
    List<TournamentDocument> findByEnrollments_Status(String status);
}
