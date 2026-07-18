package co.edu.escuelaing.techcup.tournament.domain.model;

import java.time.LocalDate;

/**
 * Filtro opcional para consultar el log de auditoría: rango de fechas, tipo de
 * evento y torneo. Todos los campos son opcionales y se combinan con AND.
 */
public record AuditEventFilter(LocalDate from, LocalDate to, String eventType, String tournamentId) {}
