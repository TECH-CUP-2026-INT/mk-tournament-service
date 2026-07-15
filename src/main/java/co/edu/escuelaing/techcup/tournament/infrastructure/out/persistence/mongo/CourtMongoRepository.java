package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.CourtDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CourtMongoRepository extends MongoRepository<CourtDocument, String> {
    Optional<CourtDocument> findByMatchId(String matchId);
    List<CourtDocument> findAllByTournamentId(String tournamentId);
}
