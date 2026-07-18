package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.TournamentParticipantDocument;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.TournamentParticipantPersistenceMapper;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.TournamentParticipantMongoRepository;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentParticipant;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentParticipantRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class TournamentParticipantRepositoryAdapter implements TournamentParticipantRepositoryPort {

    private final TournamentParticipantMongoRepository mongoRepository;
    private final TournamentParticipantPersistenceMapper mapper;

    public TournamentParticipantRepositoryAdapter(TournamentParticipantMongoRepository mongoRepository,
                                                   TournamentParticipantPersistenceMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public TournamentParticipant save(TournamentParticipant participant) {
        TournamentParticipantDocument saved = mongoRepository.save(mapper.toDocument(participant));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TournamentParticipant> findByTournamentIdAndUserId(UUID tournamentId, UUID userId) {
        return mongoRepository.findByTournamentIdAndUserId(tournamentId, userId)
                .map(mapper::toDomain);
    }
}
