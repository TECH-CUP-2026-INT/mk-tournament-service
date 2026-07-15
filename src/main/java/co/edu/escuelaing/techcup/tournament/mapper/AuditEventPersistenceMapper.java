package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.entity.document.AuditEventDocument;
import co.edu.escuelaing.techcup.tournament.service.AuditEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
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
