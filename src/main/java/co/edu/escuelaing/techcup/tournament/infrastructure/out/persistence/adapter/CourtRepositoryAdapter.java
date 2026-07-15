package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.CourtDocument;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.CourtPersistenceMapper;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.CourtMongoRepository;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.CourtRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.List;
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

    @Override
    public List<Court> findAllByTournamentId(String tournamentId) {
        return mongoRepository.findAllByTournamentId(tournamentId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
