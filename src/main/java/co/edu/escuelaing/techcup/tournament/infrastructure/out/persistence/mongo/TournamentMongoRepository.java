package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.TournamentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentMongoRepository extends MongoRepository<TournamentDocument, String> {
    List<TournamentDocument> findAllByStatus(String status);
    Optional<TournamentDocument> findByIdAndStatus(String id, String status);
    Optional<TournamentDocument> findByMatchesMatchId(String matchId);
    List<TournamentDocument> findByEnrollments_Status(String status);
}