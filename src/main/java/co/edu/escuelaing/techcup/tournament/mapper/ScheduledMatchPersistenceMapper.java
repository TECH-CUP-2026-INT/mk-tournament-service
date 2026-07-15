package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.entity.document.ScheduledMatchDocument;
import co.edu.escuelaing.techcup.tournament.service.ScheduledMatch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
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
