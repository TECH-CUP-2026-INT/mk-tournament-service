package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TournamentMongoRepository extends MongoRepository<TournamentDocument, UUID> {
    List<TournamentDocument> findAllByStatus(String status);
    Optional<TournamentDocument> findByIdAndStatus(UUID id, String status);
    Optional<TournamentDocument> findByMatchesMatchId(UUID matchId);
    List<TournamentDocument> findByEnrollments_Status(String status);

    // TC-24 (cc-teams-service): un equipo no puede actualizarse mientras esta
    // inscrito (Enrollment ENROLLED) en un torneo ACTIVE o IN_PROGRESS. El
    // $elemMatch es necesario para exigir que teamId y status ENROLLED
    // coincidan en el MISMO elemento del arreglo enrollments (no en elementos
    // distintos que cumplan cada condicion por separado).
    @Query(value = "{ 'status': { '$in': ['ACTIVE', 'IN_PROGRESS'] }, "
            + "'enrollments': { '$elemMatch': { 'teamId': ?0, 'status': 'ENROLLED' } } }", exists = true)
    boolean existsActiveEnrollmentForTeam(UUID teamId);
}
