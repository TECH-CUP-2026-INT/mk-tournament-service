package co.edu.escuelaing.techcup.tournament.domain.model;

import java.time.LocalDate;

public record AuditEventFilter(LocalDate from, LocalDate to, String eventType, String tournamentId) {}
