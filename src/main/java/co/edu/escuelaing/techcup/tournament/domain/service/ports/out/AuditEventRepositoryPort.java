package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.tournament.domain.model.AuditEventFilter;

import java.util.List;

/**
 * Puerto de persistencia para eventos de auditoría.
 */
public interface AuditEventRepositoryPort {
    AuditEvent save(AuditEvent event);
    List<AuditEvent> search(AuditEventFilter filter);
}
