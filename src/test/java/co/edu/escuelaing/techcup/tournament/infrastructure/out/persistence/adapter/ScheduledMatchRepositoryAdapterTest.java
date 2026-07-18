package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.ScheduledMatchDocument;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.ScheduledMatchMongoRepository;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Month;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScheduledMatchRepositoryAdapterTest {

    private final co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.ScheduledMatchPersistenceMapper mapper = Mappers.getMapper(co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.ScheduledMatchPersistenceMapper.class);

    private final UUID scheduledMatchId = UUID.randomUUID();
    private final UUID matchupId = UUID.randomUUID();
    private final UUID courtId = UUID.randomUUID();
    private final UUID refereeId = UUID.randomUUID();

    @Test
    void save_delegaAlMongoRepositoryYMapeaDeVuelta() {
        ScheduledMatchMongoRepository mongoRepository = mock(ScheduledMatchMongoRepository.class);
        ScheduledMatch scheduledMatch = ScheduledMatch.create(matchupId, courtId, refereeId,
                LocalDate.of(2026, Month.AUGUST, 5), LocalTime.of(9, 0));
        ScheduledMatchDocument saved = new ScheduledMatchDocument(scheduledMatchId, matchupId, courtId, refereeId,
                LocalDate.of(2026, Month.AUGUST, 5), LocalTime.of(9, 0));
        when(mongoRepository.save(any())).thenReturn(saved);

        ScheduledMatchRepositoryAdapter adapter = new ScheduledMatchRepositoryAdapter(mongoRepository, mapper);
        ScheduledMatch result = adapter.save(scheduledMatch);

        assertEquals(scheduledMatchId, result.getId());
    }

    @Test
    void existsConflict_cuandoHayConflicto_retornaTrue() {
        ScheduledMatchMongoRepository mongoRepository = mock(ScheduledMatchMongoRepository.class);
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 5);
        LocalTime time = LocalTime.of(9, 0);
        when(mongoRepository.existsByCourtIdAndMatchDateAndMatchTimeOrRefereeIdAndMatchDateAndMatchTime(
                courtId, date, time, refereeId, date, time)).thenReturn(true);

        ScheduledMatchRepositoryAdapter adapter = new ScheduledMatchRepositoryAdapter(mongoRepository, mapper);

        assertTrue(adapter.existsConflict(courtId, refereeId, date, time));
    }

    @Test
    void existsConflict_cuandoNoHayConflicto_retornaFalse() {
        ScheduledMatchMongoRepository mongoRepository = mock(ScheduledMatchMongoRepository.class);
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 5);
        LocalTime time = LocalTime.of(9, 0);
        when(mongoRepository.existsByCourtIdAndMatchDateAndMatchTimeOrRefereeIdAndMatchDateAndMatchTime(
                courtId, date, time, refereeId, date, time)).thenReturn(false);

        ScheduledMatchRepositoryAdapter adapter = new ScheduledMatchRepositoryAdapter(mongoRepository, mapper);

        assertFalse(adapter.existsConflict(courtId, refereeId, date, time));
    }
}
