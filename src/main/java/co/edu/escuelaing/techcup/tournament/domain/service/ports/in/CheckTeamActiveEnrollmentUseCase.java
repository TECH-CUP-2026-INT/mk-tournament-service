package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import java.util.UUID;

/**
 * Punto de integración para cc-teams-service (TC-24: un equipo no puede
 * actualizarse mientras esté inscrito en un torneo activo o en progreso).
 */
public interface CheckTeamActiveEnrollmentUseCase {
    boolean hasActiveEnrollment(UUID teamId);
}
