package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.tournament.domain.model.AuditEventFilter;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.AuditEventRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ConsultAuditEventsUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultAuditEventsService implements ConsultAuditEventsUseCase {

    private final AuditEventRepositoryPort repository;


    @Override
    public List<AuditEvent> consult(AuditEventFilter filter) {
        return repository.search(filter);
    }
}
