package co.edu.escuelaing.techcup.tournament.repository.mongo;

import co.edu.escuelaing.techcup.tournament.entity.document.TournamentParticipantDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface TournamentParticipantMongoRepository extends MongoRepository<TournamentParticipantDocument, String> {
    Optional<TournamentParticipantDocument> findByTournamentIdAndUserId(String tournamentId, String userId);
}
