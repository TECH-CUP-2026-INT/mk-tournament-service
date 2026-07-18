package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.AuditEventResponse;
import co.edu.escuelaing.techcup.tournament.application.mapper.AuditEventRestMapper;
import co.edu.escuelaing.techcup.tournament.domain.model.AuditEventFilter;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ConsultAuditEventsUseCase;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller.swagger.AuditEventControllerSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * TC-40: audit log query. Currently without real access control (see
 * SecurityConfig) — pending the future Identity Service (JWT + Admin/Organizer roles).
 */
@RestController
@RequestMapping("/audit-events")
@RequiredArgsConstructor
public class AuditEventController implements AuditEventControllerSwagger {

    private final ConsultAuditEventsUseCase consultAuditEventsUseCase;
    private final AuditEventRestMapper mapper;

    @Override
    @GetMapping
    public ResponseEntity<List<AuditEventResponse>> consult(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String tournamentId) {

        List<AuditEventResponse> result = consultAuditEventsUseCase
                .consult(new AuditEventFilter(from, to, eventType, tournamentId))
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(result);
    }
}
