package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourtMongoRepository extends MongoRepository<CourtDocument, UUID> {
    Optional<CourtDocument> findByMatchId(UUID matchId);
    List<CourtDocument> findAllByTournamentId(UUID tournamentId);
}
