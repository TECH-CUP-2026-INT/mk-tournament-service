package co.edu.escuelaing.techcup.tournament.repository.mongo;

import co.edu.escuelaing.techcup.tournament.entity.document.TournamentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TournamentMongoRepository extends MongoRepository<TournamentDocument, String> {
}