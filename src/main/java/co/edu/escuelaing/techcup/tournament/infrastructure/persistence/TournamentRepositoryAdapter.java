package co.edu.escuelaing.techcup.tournament.infrastructure.persistence;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.port.out.TournamentRepositoryPort;
import co.edu.escuelaing.techcup.tournament.infrastructure.persistence.mapper.TournamentPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TournamentRepositoryAdapter implements TournamentRepositoryPort {

    private final TournamentJpaRepository mongoRepository;

    public TournamentRepositoryAdapter(TournamentJpaRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Optional<Tournament> findById(String id) {
        return mongoRepository.findById(id)
                .map(TournamentPersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }
}
