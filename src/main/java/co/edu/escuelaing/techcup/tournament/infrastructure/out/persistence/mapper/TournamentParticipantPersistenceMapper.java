package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.TournamentParticipantDocument;
import co.edu.escuelaing.techcup.tournament.domain.model.ParticipantStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentParticipant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
/**
 * Mapper MapStruct: {@link co.edu.escuelaing.techcup.tournament.domain.model.TournamentParticipant}
 * ↔ documento de Mongo.
 */
public interface TournamentParticipantPersistenceMapper {

    default TournamentParticipant toDomain(TournamentParticipantDocument document) {
        return TournamentParticipant.reconstruct(
                document.getId(),
                document.getTournamentId(),
                document.getUserId(),
                ParticipantStatus.valueOf(document.getStatus())
        );
    }

    default TournamentParticipantDocument toDocument(TournamentParticipant domain) {
        return new TournamentParticipantDocument(
                domain.getId(),
                domain.getTournamentId(),
                domain.getUserId(),
                domain.getStatus().name()
        );
    }
}
