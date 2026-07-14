package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.AuditEvent;
import co.edu.escuelaing.techcup.tournament.service.AuditEventFilter;

import java.util.List;

public interface AuditEventRepositoryPort {
    AuditEvent save(AuditEvent event);
    List<AuditEvent> search(AuditEventFilter filter);
}
