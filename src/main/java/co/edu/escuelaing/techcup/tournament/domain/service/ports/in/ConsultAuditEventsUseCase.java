package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.tournament.domain.model.AuditEventFilter;

import java.util.List;

public interface ConsultAuditEventsUseCase {
    List<AuditEvent> consult(AuditEventFilter filter);
}
