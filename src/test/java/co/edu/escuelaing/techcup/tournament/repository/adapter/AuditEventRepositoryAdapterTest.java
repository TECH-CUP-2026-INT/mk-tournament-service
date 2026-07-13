package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.entity.document.AuditEventDocument;
import co.edu.escuelaing.techcup.tournament.service.AuditEventFilter;
import org.bson.Document;
import org.junit.jupiter.api.Test;
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

    @Test
    void search_sinFiltros_noAgregaCondicionesAlQuery() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        when(mongoTemplate.find(any(), eq(AuditEventDocument.class))).thenReturn(List.of());

        AuditEventRepositoryAdapter adapter = new AuditEventRepositoryAdapter(mongoTemplate);
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

        AuditEventRepositoryAdapter adapter = new AuditEventRepositoryAdapter(mongoTemplate);
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
