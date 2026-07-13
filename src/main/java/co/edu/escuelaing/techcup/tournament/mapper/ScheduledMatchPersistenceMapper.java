package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.entity.document.ScheduledMatchDocument;
import co.edu.escuelaing.techcup.tournament.service.ScheduledMatch;

public class ScheduledMatchPersistenceMapper {

    private ScheduledMatchPersistenceMapper() {}

    public static ScheduledMatch toDomain(ScheduledMatchDocument document) {
        return ScheduledMatch.reconstruct(
                document.getId(),
                document.getMatchupId(),
                document.getCourtId(),
                document.getRefereeId(),
                document.getMatchDate(),
                document.getMatchTime()
        );
    }

    public static ScheduledMatchDocument toDocument(ScheduledMatch domain) {
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
