package co.edu.escuelaing.techcup.tournament.domain.model;

/**
 * Estado de registro de un equipo en un torneo (vista legacy de solo nombre y
 * estado; para el flujo de pago ver {@link EnrollmentStatus}).
 */
public enum RegistrationStatus {
    PENDING,
    APPROVED,
    REJECTED,
    DISQUALIFIED,
    INACTIVE
}
