package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.tournament.domain.model.AuditEventFilter;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.AuditEventRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsultAuditEventsServiceTest {

    @Test
    void consult_delegaAlRepositorioConElFiltroRecibido() {
        AuditEventRepositoryPort repository = mock(AuditEventRepositoryPort.class);
        AuditEventFilter filter = new AuditEventFilter(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), "CreateTournamentService.create", "t1");
        AuditEvent event = AuditEvent.create("system", "CreateTournamentService.create", "t1");

        when(repository.search(filter)).thenReturn(List.of(event));

        ConsultAuditEventsService service = new ConsultAuditEventsService(repository);
        List<AuditEvent> result = service.consult(filter);

        assertEquals(1, result.size());
        verify(repository).search(filter);
    }
}
