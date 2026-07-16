package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.PlayerSanctionPersistenceMapper;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.PlayerSanctionMongoRepository;
import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PlayerSanctionRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PlayerSanctionRepositoryAdapter implements PlayerSanctionRepositoryPort {

    private final PlayerSanctionMongoRepository mongoRepository;
    private final PlayerSanctionPersistenceMapper mapper;

    public PlayerSanctionRepositoryAdapter(PlayerSanctionMongoRepository mongoRepository,
                                            PlayerSanctionPersistenceMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public PlayerSanction save(PlayerSanction sanction) {
        var saved = mongoRepository.save(mapper.toDocument(sanction));
        return mapper.toDomain(saved);
    }

    @Override
    public List<PlayerSanction> findActiveByPlayerId(UUID playerId) {
        return mongoRepository.findByPlayerIdAndMatchesRemainingGreaterThan(playerId, 0).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<PlayerSanction> findAllActive() {
        return mongoRepository.findByMatchesRemainingGreaterThan(0).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
