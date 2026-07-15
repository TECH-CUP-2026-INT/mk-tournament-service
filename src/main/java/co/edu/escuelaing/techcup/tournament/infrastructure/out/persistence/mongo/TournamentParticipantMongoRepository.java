package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.TournamentParticipantDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface TournamentParticipantMongoRepository extends MongoRepository<TournamentParticipantDocument, String> {
    Optional<TournamentParticipantDocument> findByTournamentIdAndUserId(String tournamentId, String userId);
}
