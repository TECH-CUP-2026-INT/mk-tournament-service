package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.entity.document.AuditEventDocument;
import co.edu.escuelaing.techcup.tournament.service.AuditEvent;

public class AuditEventPersistenceMapper {

    private AuditEventPersistenceMapper() {}

    public static AuditEvent toDomain(AuditEventDocument document) {
        return AuditEvent.reconstruct(
                document.getId(),
                document.getTimestamp(),
                document.getActor(),
                document.getActionType(),
                document.getAffectedEntityId()
        );
    }

    public static AuditEventDocument toDocument(AuditEvent domain) {
        return new AuditEventDocument(
                domain.getId(),
                domain.getTimestamp(),
                domain.getActor(),
                domain.getActionType(),
                domain.getAffectedEntityId()
        );
    }
}
