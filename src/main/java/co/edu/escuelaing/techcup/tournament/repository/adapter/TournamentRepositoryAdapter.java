package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import co.edu.escuelaing.techcup.tournament.mapper.TournamentPersistenceMapper;
import co.edu.escuelaing.techcup.tournament.entity.document.TournamentDocument;
import co.edu.escuelaing.techcup.tournament.repository.mongo.TournamentMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TournamentRepositoryAdapter implements TournamentRepositoryPort {

    private final TournamentMongoRepository mongoRepository;

    public TournamentRepositoryAdapter(TournamentMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Tournament save(Tournament tournament) {
        TournamentDocument saved = mongoRepository.save(TournamentPersistenceMapper.toDocument(tournament));
        return TournamentPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Tournament> findById(String id) {
        return mongoRepository.findById(id).map(TournamentPersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public List<Tournament> findAllByStatus(TournamentStatus status) {
        return mongoRepository.findAllByStatus(status.name()).stream()
                .map(TournamentPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Tournament> findByIdAndStatus(String id, TournamentStatus status) {
        return mongoRepository.findByIdAndStatus(id, status.name())
                .map(TournamentPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Tournament> findByMatchId(String matchId) {
        return mongoRepository.findByMatchesMatchId(matchId)
                .map(TournamentPersistenceMapper::toDomain);
    }
}
