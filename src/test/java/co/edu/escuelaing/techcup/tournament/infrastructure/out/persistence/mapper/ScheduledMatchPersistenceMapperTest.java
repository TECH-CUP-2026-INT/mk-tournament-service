package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.ScheduledMatchDocument;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Month;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScheduledMatchPersistenceMapperTest {

    private final ScheduledMatchPersistenceMapper mapper = Mappers.getMapper(ScheduledMatchPersistenceMapper.class);

    @Test
    void toDomain_convierteTodosLosCampos() {
        UUID id = UUID.randomUUID();
        UUID matchupId = UUID.randomUUID();
        UUID courtId = UUID.randomUUID();
        UUID refereeId = UUID.randomUUID();
        ScheduledMatchDocument document = new ScheduledMatchDocument(
                id, matchupId, courtId, refereeId, LocalDate.of(2026, Month.AUGUST, 5), LocalTime.of(9, 0));

        ScheduledMatch scheduledMatch = mapper.toDomain(document);

        assertEquals(id, scheduledMatch.getId());
        assertEquals(matchupId, scheduledMatch.getMatchupId());
        assertEquals(courtId, scheduledMatch.getCourtId());
        assertEquals(refereeId, scheduledMatch.getRefereeId());
        assertEquals(LocalDate.of(2026, Month.AUGUST, 5), scheduledMatch.getMatchDate());
        assertEquals(LocalTime.of(9, 0), scheduledMatch.getMatchTime());
    }

    @Test
    void toDocument_convierteTodosLosCampos() {
        UUID id = UUID.randomUUID();
        UUID matchupId = UUID.randomUUID();
        UUID courtId = UUID.randomUUID();
        UUID refereeId = UUID.randomUUID();
        ScheduledMatch scheduledMatch = ScheduledMatch.reconstruct(
                id, matchupId, courtId, refereeId, LocalDate.of(2026, Month.AUGUST, 5), LocalTime.of(9, 0));

        ScheduledMatchDocument document = mapper.toDocument(scheduledMatch);

        assertEquals(id, document.getId());
        assertEquals(matchupId, document.getMatchupId());
        assertEquals(courtId, document.getCourtId());
        assertEquals(refereeId, document.getRefereeId());
        assertEquals(LocalDate.of(2026, Month.AUGUST, 5), document.getMatchDate());
        assertEquals(LocalTime.of(9, 0), document.getMatchTime());
    }
}
