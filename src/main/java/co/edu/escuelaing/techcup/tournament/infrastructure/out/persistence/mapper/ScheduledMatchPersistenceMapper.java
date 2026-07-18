package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.ScheduledMatchDocument;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
/**
 * Mapper MapStruct: {@link co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch}
 * ↔ documento de Mongo.
 */
public interface ScheduledMatchPersistenceMapper {

    default ScheduledMatch toDomain(ScheduledMatchDocument document) {
        return ScheduledMatch.reconstruct(
                document.getId(),
                document.getMatchupId(),
                document.getCourtId(),
                document.getRefereeId(),
                document.getMatchDate(),
                document.getMatchTime()
        );
    }

    default ScheduledMatchDocument toDocument(ScheduledMatch domain) {
        return new ScheduledMatchDocument(
                domain.getId(),
                domain.getMatchupId(),
                domain.getCourtId(),
                domain.getRefereeId(),
                domain.getMatchDate(),
                domain.getMatchTime()
        );
    }
}
