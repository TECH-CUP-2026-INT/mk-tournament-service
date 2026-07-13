package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.service.AuditEvent;
import co.edu.escuelaing.techcup.tournament.service.AuditEventFilter;
import co.edu.escuelaing.techcup.tournament.service.ports.AuditEventRepositoryPort;
import co.edu.escuelaing.techcup.tournament.service.ports.ConsultAuditEventsUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultAuditEventsService implements ConsultAuditEventsUseCase {

    private final AuditEventRepositoryPort repository;

    public ConsultAuditEventsService(AuditEventRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<AuditEvent> consult(AuditEventFilter filter) {
        return repository.search(filter);
    }
}
