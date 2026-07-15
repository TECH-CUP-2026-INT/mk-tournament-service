package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.application.usecase.CreateTournamentService;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.AuditEventDocument;
import co.edu.escuelaing.techcup.tournament.domain.model.AuditEventFilter;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditEventRepositoryAdapterTest {

    private final co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.AuditEventPersistenceMapper mapper = Mappers.getMapper(co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.AuditEventPersistenceMapper.class);

    @Test
    void search_sinFiltros_noAgregaCondicionesAlQuery() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        when(mongoTemplate.find(any(), eq(AuditEventDocument.class))).thenReturn(List.of());

        AuditEventRepositoryAdapter adapter = new AuditEventRepositoryAdapter(mongoTemplate, mapper);
        adapter.search(new AuditEventFilter(null, null, null, null));

        var captor = forClass(Query.class);
        verify(mongoTemplate).find(captor.capture(), eq(AuditEventDocument.class));

        Document queryObject = captor.getValue().getQueryObject();
        assertTrue(queryObject.isEmpty());
    }

    @Test
    void search_conTodosLosFiltros_combinaTodoConAnd() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        when(mongoTemplate.find(any(), eq(AuditEventDocument.class))).thenReturn(List.of());

        AuditEventRepositoryAdapter adapter = new AuditEventRepositoryAdapter(mongoTemplate, mapper);
        adapter.search(new AuditEventFilter(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31),
                "CreateTournamentService.create", "t1"));

        var captor = forClass(Query.class);
        verify(mongoTemplate).find(captor.capture(), eq(AuditEventDocument.class));

        Document queryObject = captor.getValue().getQueryObject();
        assertFalse(queryObject.isEmpty());
        assertTrue(queryObject.containsKey("$and"));
        assertEquals(4, ((List<?>) queryObject.get("$and")).size());
    }
}
