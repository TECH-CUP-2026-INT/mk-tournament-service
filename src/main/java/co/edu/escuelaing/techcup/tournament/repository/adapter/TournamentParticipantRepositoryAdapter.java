package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.entity.document.TournamentParticipantDocument;
import co.edu.escuelaing.techcup.tournament.repository.mongo.TournamentParticipantMongoRepository;
import co.edu.escuelaing.techcup.tournament.service.ParticipantStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentParticipant;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentParticipantRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class TournamentParticipantRepositoryAdapter implements TournamentParticipantRepositoryPort {

    private final TournamentParticipantMongoRepository mongoRepository;

    public TournamentParticipantRepositoryAdapter(TournamentParticipantMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public TournamentParticipant save(TournamentParticipant participant) {
        TournamentParticipantDocument doc = new TournamentParticipantDocument(
                participant.getId(),
                participant.getTournamentId(),
                participant.getUserId(),
                participant.getStatus().name()
        );
        TournamentParticipantDocument saved = mongoRepository.save(doc);
        return TournamentParticipant.reconstruct(saved.getId(), saved.getTournamentId(),
                saved.getUserId(), ParticipantStatus.valueOf(saved.getStatus()));
    }

    @Override
    public Optional<TournamentParticipant> findByTournamentIdAndUserId(String tournamentId, String userId) {
        return mongoRepository.findByTournamentIdAndUserId(tournamentId, userId)
                .map(doc -> TournamentParticipant.reconstruct(doc.getId(), doc.getTournamentId(),
                        doc.getUserId(), ParticipantStatus.valueOf(doc.getStatus())));
    }
}
