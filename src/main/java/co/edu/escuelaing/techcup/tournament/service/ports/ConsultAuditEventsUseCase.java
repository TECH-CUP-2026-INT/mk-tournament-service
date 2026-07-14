package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.AuditEvent;
import co.edu.escuelaing.techcup.tournament.service.AuditEventFilter;

import java.util.List;

public interface ConsultAuditEventsUseCase {
    List<AuditEvent> consult(AuditEventFilter filter);
}
