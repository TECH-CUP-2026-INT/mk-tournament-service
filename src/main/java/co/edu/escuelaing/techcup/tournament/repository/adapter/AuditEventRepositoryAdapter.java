package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.entity.document.AuditEventDocument;
import co.edu.escuelaing.techcup.tournament.mapper.AuditEventPersistenceMapper;
import co.edu.escuelaing.techcup.tournament.service.AuditEvent;
import co.edu.escuelaing.techcup.tournament.service.AuditEventFilter;
import co.edu.escuelaing.techcup.tournament.service.ports.AuditEventRepositoryPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Único punto del proyecto que usa MongoTemplate directamente en vez de una
 * interfaz MongoRepository: los 3 filtros de esta consulta son opcionales y
 * se combinan con AND, lo cual no se puede expresar limpiamente con un
 * método de query derivada (requeriría un método por cada una de las 8
 * combinaciones posibles). Con MongoTemplate se arma un Criteria dinámico
 * que solo incluye los filtros presentes.
 */
@Component
public class AuditEventRepositoryAdapter implements AuditEventRepositoryPort {

    private final MongoTemplate mongoTemplate;
    private final AuditEventPersistenceMapper mapper;

    public AuditEventRepositoryAdapter(MongoTemplate mongoTemplate, AuditEventPersistenceMapper mapper) {
        this.mongoTemplate = mongoTemplate;
        this.mapper = mapper;
    }

    @Override
    public AuditEvent save(AuditEvent event) {
        AuditEventDocument saved = mongoTemplate.save(mapper.toDocument(event));
        return mapper.toDomain(saved);
    }

    @Override
    public List<AuditEvent> search(AuditEventFilter filter) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (filter.from() != null) {
            criteriaList.add(Criteria.where("timestamp")
                    .gte(filter.from().atStartOfDay(ZoneOffset.UTC).toInstant()));
        }
        if (filter.to() != null) {
            criteriaList.add(Criteria.where("timestamp")
                    .lt(filter.to().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()));
        }
        if (filter.eventType() != null && !filter.eventType().isBlank()) {
            criteriaList.add(Criteria.where("actionType").is(filter.eventType()));
        }
        if (filter.tournamentId() != null && !filter.tournamentId().isBlank()) {
            criteriaList.add(Criteria.where("affectedEntityId").is(filter.tournamentId()));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, AuditEventDocument.class).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
