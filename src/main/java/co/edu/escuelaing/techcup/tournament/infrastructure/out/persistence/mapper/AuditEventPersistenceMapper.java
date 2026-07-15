package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.AuditEventDocument;
import co.edu.escuelaing.techcup.tournament.domain.model.AuditEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
/**
 * Mapper MapStruct: {@link co.edu.escuelaing.techcup.tournament.domain.model.AuditEvent}
 * ↔ documento de Mongo.
 */
public interface AuditEventPersistenceMapper {

    default AuditEvent toDomain(AuditEventDocument document) {
        return AuditEvent.reconstruct(
                document.getId(),
                document.getTimestamp(),
                document.getActor(),
                document.getActionType(),
                document.getAffectedEntityId()
        );
    }

    default AuditEventDocument toDocument(AuditEvent domain) {
        return new AuditEventDocument(
                domain.getId(),
                domain.getTimestamp(),
                domain.getActor(),
                domain.getActionType(),
                domain.getAffectedEntityId()
        );
    }
}
