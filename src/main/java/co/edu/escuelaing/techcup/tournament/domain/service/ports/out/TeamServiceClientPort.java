package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import java.util.UUID;

/**
 * Puerto hacia el Team Service.
 */
public interface TeamServiceClientPort {

    /**
     * Consulta el nombre y el tamaño de roster de un equipo en el Team Service.
     * A diferencia de {@link PaymentServiceClientPort#getOrderStatus(UUID)},
     * SÍ propaga el error si el Team Service no responde: no hay una manera segura
     * de inscribir un equipo sin poder validar su roster.
     */
    TeamInfo getTeamInfo(UUID teamId);

    record TeamInfo(String teamName, int rosterSize) {}
}
