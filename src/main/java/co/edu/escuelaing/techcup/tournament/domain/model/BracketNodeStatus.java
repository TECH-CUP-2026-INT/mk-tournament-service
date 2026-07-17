package co.edu.escuelaing.techcup.tournament.domain.model;

/**
 * Estado de un nodo de la llave eliminatoria.
 */
public enum BracketNodeStatus {
    /** Uno o los dos cupos siguen "Por definir" (esperando el resultado de una ronda previa). */
    PENDING_SLOTS,
    /** Los dos cupos están definidos y el partido ya fue creado (matchId asignado); falta jugarse. */
    SCHEDULED,
    /** El partido terminó empatado en tiempo reglamentario y no llegó un ganador externo; espera resolución manual. */
    PENDING_PENALTIES,
    /** El partido terminó y el ganador ya avanzó (o, si era la Final, ya se asignó campeón). */
    FINISHED
}
