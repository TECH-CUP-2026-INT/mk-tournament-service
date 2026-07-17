package co.edu.escuelaing.techcup.tournament.domain.model;

/**
 * Fase de competencia a la que pertenece un partido. Nombres en español porque
 * coinciden con el campo {@code fase} del contrato de evento acordado con
 * Matches ({@code techcup.match.finished}) y con el body de {@code /sim}.
 */
public enum MatchPhase {
    GRUPOS,
    ELIMINATORIA
}
