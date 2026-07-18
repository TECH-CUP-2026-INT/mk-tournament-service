package co.edu.escuelaing.techcup.tournament.domain.model;

/**
 * Estados posibles de una inscripción, desde la reserva del cupo hasta su
 * confirmación, rechazo o expiración.
 */
public enum EnrollmentStatus {
    RESERVED,
    PENDING_PAYMENT,
    ENROLLED,
    REJECTED,
    EXPIRED
}
