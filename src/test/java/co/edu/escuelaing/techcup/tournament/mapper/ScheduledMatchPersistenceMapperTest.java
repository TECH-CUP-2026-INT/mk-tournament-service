package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.entity.document.ScheduledMatchDocument;
import co.edu.escuelaing.techcup.tournament.service.ScheduledMatch;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScheduledMatchPersistenceMapperTest {

    private final ScheduledMatchPersistenceMapper mapper = Mappers.getMapper(ScheduledMatchPersistenceMapper.class);

    @Test
    void toDomain_convierteTodosLosCampos() {
        ScheduledMatchDocument document = new ScheduledMatchDocument(
                "sm1", "m01", "court-1", "ref-1", LocalDate.of(2026, 8, 5), LocalTime.of(9, 0));

        ScheduledMatch scheduledMatch = mapper.toDomain(document);

        assertEquals("sm1", scheduledMatch.getId());
        assertEquals("m01", scheduledMatch.getMatchupId());
        assertEquals("court-1", scheduledMatch.getCourtId());
        assertEquals("ref-1", scheduledMatch.getRefereeId());
        assertEquals(LocalDate.of(2026, 8, 5), scheduledMatch.getMatchDate());
        assertEquals(LocalTime.of(9, 0), scheduledMatch.getMatchTime());
    }

    @Test
    void toDocument_convierteTodosLosCampos() {
        ScheduledMatch scheduledMatch = ScheduledMatch.reconstruct(
                "sm1", "m01", "court-1", "ref-1", LocalDate.of(2026, 8, 5), LocalTime.of(9, 0));

        ScheduledMatchDocument document = mapper.toDocument(scheduledMatch);

        assertEquals("sm1", document.getId());
        assertEquals("m01", document.getMatchupId());
        assertEquals("court-1", document.getCourtId());
        assertEquals("ref-1", document.getRefereeId());
        assertEquals(LocalDate.of(2026, 8, 5), document.getMatchDate());
        assertEquals(LocalTime.of(9, 0), document.getMatchTime());
    }
}
