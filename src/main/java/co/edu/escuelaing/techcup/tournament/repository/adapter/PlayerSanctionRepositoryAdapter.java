package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.mapper.PlayerSanctionPersistenceMapper;
import co.edu.escuelaing.techcup.tournament.repository.mongo.PlayerSanctionMongoRepository;
import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.service.ports.PlayerSanctionRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public List<PlayerSanction> findActiveByPlayerId(String playerId) {
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
