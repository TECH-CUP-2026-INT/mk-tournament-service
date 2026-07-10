// src/main/java/co/edu/escuelaing/techcup/tournament/infrastructure/persistence/TournamentMongoRepository.java
package co.edu.escuelaing.techcup.tournament.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TournamentMongoRepository extends MongoRepository<TournamentDocument, String> {
}