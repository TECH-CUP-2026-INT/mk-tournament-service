package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.infrastructure.config.SecurityConfig;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.AuditEventResponse;
import co.edu.escuelaing.techcup.tournament.application.mapper.AuditEventRestMapper;
import co.edu.escuelaing.techcup.tournament.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.tournament.domain.model.AuditEventFilter;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ConsultAuditEventsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditEventController.class)
@Import(SecurityConfig.class)
class AuditEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private ConsultAuditEventsUseCase consultAuditEventsUseCase;
    @MockitoBean private AuditEventRestMapper mapper;

    @Test
    void consult_sinFiltros_devuelve200() throws Exception {
        AuditEvent event = AuditEvent.reconstruct(UUID.randomUUID(), Instant.parse("2026-07-01T00:00:00Z"),
                "system", "CreateTournamentService.create", "t1");
        when(consultAuditEventsUseCase.consult(any())).thenReturn(List.of(event));
        when(mapper.toResponse(any())).thenReturn(new AuditEventResponse(
                Instant.parse("2026-07-01T00:00:00Z"), "system", "CreateTournamentService.create", "t1"));

        mockMvc.perform(get("/audit-events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actionType").value("CreateTournamentService.create"))
                .andExpect(jsonPath("$[0].affectedEntityId").value("t1"));
    }

    @Test
    void consult_conFiltros_pasaFiltroCorrectoAlUseCase() throws Exception {
        when(consultAuditEventsUseCase.consult(any())).thenReturn(List.of());

        mockMvc.perform(get("/audit-events")
                        .param("from", "2026-01-01")
                        .param("to", "2026-01-31")
                        .param("eventType", "CreateTournamentService.create")
                        .param("tournamentId", "t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(consultAuditEventsUseCase).consult(new AuditEventFilter(
                java.time.LocalDate.of(2026, Month.JANUARY, 1), java.time.LocalDate.of(2026, Month.JANUARY, 31),
                "CreateTournamentService.create", "t1"));
    }
}
