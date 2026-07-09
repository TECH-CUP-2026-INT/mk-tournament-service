package co.edu.escuelaing.techcup.tournament.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TournamentJpaRepository extends MongoRepository<TournamentEntity, String> {
}
