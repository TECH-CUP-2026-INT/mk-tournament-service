package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.UUID;

public interface TournamentParticipantMongoRepository extends MongoRepository<TournamentParticipantDocument, UUID> {
    Optional<TournamentParticipantDocument> findByTournamentIdAndUserId(UUID tournamentId, UUID userId);
}
