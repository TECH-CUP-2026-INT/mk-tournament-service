package co.edu.escuelaing.techcup.tournament.controller.impl;

import co.edu.escuelaing.techcup.tournament.dto.response.AuditEventResponse;
import co.edu.escuelaing.techcup.tournament.service.AuditEventFilter;
import co.edu.escuelaing.techcup.tournament.service.ports.ConsultAuditEventsUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * TC-40: consulta del log de auditoría. Hoy sin control de acceso real
 * (ver SecurityConfig) — pendiente del futuro Servicio de Identidad
 * (JWT + roles Admin/Organizer).
 */
@RestController
@RequestMapping("/audit-events")
public class AuditEventController {

    private final ConsultAuditEventsUseCase consultAuditEventsUseCase;

    public AuditEventController(ConsultAuditEventsUseCase consultAuditEventsUseCase) {
        this.consultAuditEventsUseCase = consultAuditEventsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<AuditEventResponse>> consult(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String tournamentId) {

        List<AuditEventResponse> result = consultAuditEventsUseCase
                .consult(new AuditEventFilter(from, to, eventType, tournamentId))
                .stream()
                .map(e -> new AuditEventResponse(e.getTimestamp(), e.getActor(), e.getActionType(), e.getAffectedEntityId()))
                .toList();

        return ResponseEntity.ok(result);
    }
}
