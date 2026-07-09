// src/main/java/co/edu/escuelaing/techcup/tournament/infrastructure/persistence/TournamentRepositoryAdapter.java
package co.edu.escuelaing.techcup.tournament.infrastructure.persistence;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.port.out.TournamentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TournamentRepositoryAdapter implements TournamentRepositoryPort {

    private final TournamentMongoRepository mongoRepository;

    public TournamentRepositoryAdapter(TournamentMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Tournament save(Tournament tournament) {
        TournamentDocument saved = mongoRepository.save(toDocument(tournament));
        return toDomain(saved);
    }

    @Override
    public Optional<Tournament> findById(String id) {
        return mongoRepository.findById(id).map(this::toDomain);
    }

    private TournamentDocument toDocument(Tournament tournament) {
        return new TournamentDocument(
                tournament.getId(),
                tournament.getName(),
                tournament.getNumberOfTeams(),
                tournament.getCost(),
                tournament.getStartDate(),
                tournament.getEndDate(),
                tournament.getRegistrationDeadline(),
                tournament.getStatus().name()
        );
    }

    private Tournament toDomain(TournamentDocument document) {
        return Tournament.reconstruct(
                document.getId(),
                document.getName(),
                document.getNumberOfTeams(),
                document.getCost(),
                document.getStartDate(),
                document.getEndDate(),
                document.getRegistrationDeadline(),
                TournamentStatus.valueOf(document.getStatus())
        );
    }
}