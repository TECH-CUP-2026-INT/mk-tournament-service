package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.entity.document.TournamentParticipantDocument;
import co.edu.escuelaing.techcup.tournament.mapper.TournamentParticipantPersistenceMapper;
import co.edu.escuelaing.techcup.tournament.repository.mongo.TournamentParticipantMongoRepository;
import co.edu.escuelaing.techcup.tournament.service.TournamentParticipant;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentParticipantRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.Optional;

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
    public Optional<TournamentParticipant> findByTournamentIdAndUserId(String tournamentId, String userId) {
        return mongoRepository.findByTournamentIdAndUserId(tournamentId, userId)
                .map(mapper::toDomain);
    }
}
