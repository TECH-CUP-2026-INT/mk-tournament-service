package co.edu.escuelaing.techcup.tournament.service;

import java.time.LocalDate;

public record AuditEventFilter(LocalDate from, LocalDate to, String eventType, String tournamentId) {}
