package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.entity.document.CourtDocument;
import co.edu.escuelaing.techcup.tournament.mapper.CourtPersistenceMapper;
import co.edu.escuelaing.techcup.tournament.repository.mongo.CourtMongoRepository;
import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.ports.CourtRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class CourtRepositoryAdapter implements CourtRepositoryPort {

    private final CourtMongoRepository mongoRepository;
    private final CourtPersistenceMapper mapper;

    public CourtRepositoryAdapter(CourtMongoRepository mongoRepository, CourtPersistenceMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public Court save(Court court) {
        CourtDocument saved = mongoRepository.save(mapper.toDocument(court));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Court> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Court> findByMatchId(String matchId) {
        return mongoRepository.findByMatchId(matchId).map(mapper::toDomain);
    }
}
