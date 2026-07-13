package co.edu.escuelaing.techcup.tournament.repository.mongo;

import co.edu.escuelaing.techcup.tournament.entity.document.TournamentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentMongoRepository extends MongoRepository<TournamentDocument, String> {
    List<TournamentDocument> findAllByStatus(String status);
    Optional<TournamentDocument> findByIdAndStatus(String id, String status);
    Optional<TournamentDocument> findByMatchesMatchId(String matchId);
}