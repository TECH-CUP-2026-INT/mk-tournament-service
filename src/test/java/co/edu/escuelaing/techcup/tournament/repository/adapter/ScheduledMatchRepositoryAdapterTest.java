package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.entity.document.ScheduledMatchDocument;
import co.edu.escuelaing.techcup.tournament.repository.mongo.ScheduledMatchMongoRepository;
import co.edu.escuelaing.techcup.tournament.service.ScheduledMatch;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScheduledMatchRepositoryAdapterTest {

    private final co.edu.escuelaing.techcup.tournament.mapper.ScheduledMatchPersistenceMapper mapper = Mappers.getMapper(co.edu.escuelaing.techcup.tournament.mapper.ScheduledMatchPersistenceMapper.class);

    @Test
    void save_delegaAlMongoRepositoryYMapeaDeVuelta() {
        ScheduledMatchMongoRepository mongoRepository = mock(ScheduledMatchMongoRepository.class);
        ScheduledMatch scheduledMatch = ScheduledMatch.create("m01", "court-1", "ref-1",
                LocalDate.of(2026, 8, 5), LocalTime.of(9, 0));
        ScheduledMatchDocument saved = new ScheduledMatchDocument("sm1", "m01", "court-1", "ref-1",
                LocalDate.of(2026, 8, 5), LocalTime.of(9, 0));
        when(mongoRepository.save(any())).thenReturn(saved);

        ScheduledMatchRepositoryAdapter adapter = new ScheduledMatchRepositoryAdapter(mongoRepository, mapper);
        ScheduledMatch result = adapter.save(scheduledMatch);

        assertEquals("sm1", result.getId());
    }

    @Test
    void existsConflict_cuandoHayConflicto_retornaTrue() {
        ScheduledMatchMongoRepository mongoRepository = mock(ScheduledMatchMongoRepository.class);
        LocalDate date = LocalDate.of(2026, 8, 5);
        LocalTime time = LocalTime.of(9, 0);
        when(mongoRepository.existsByCourtIdAndMatchDateAndMatchTimeOrRefereeIdAndMatchDateAndMatchTime(
                "court-1", date, time, "ref-1", date, time)).thenReturn(true);

        ScheduledMatchRepositoryAdapter adapter = new ScheduledMatchRepositoryAdapter(mongoRepository, mapper);

        assertTrue(adapter.existsConflict("court-1", "ref-1", date, time));
    }

    @Test
    void existsConflict_cuandoNoHayConflicto_retornaFalse() {
        ScheduledMatchMongoRepository mongoRepository = mock(ScheduledMatchMongoRepository.class);
        LocalDate date = LocalDate.of(2026, 8, 5);
        LocalTime time = LocalTime.of(9, 0);
        when(mongoRepository.existsByCourtIdAndMatchDateAndMatchTimeOrRefereeIdAndMatchDateAndMatchTime(
                "court-1", date, time, "ref-1", date, time)).thenReturn(false);

        ScheduledMatchRepositoryAdapter adapter = new ScheduledMatchRepositoryAdapter(mongoRepository, mapper);

        assertFalse(adapter.existsConflict("court-1", "ref-1", date, time));
    }
}
