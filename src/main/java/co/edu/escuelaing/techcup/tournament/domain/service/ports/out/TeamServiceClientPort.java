package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

/**
 * Puerto hacia el Team Service.
 */
public interface TeamServiceClientPort {

    /**
     * Consulta el nombre y el tamaño de roster de un equipo en el Team Service.
     * A diferencia de {@link PaymentServiceClientPort#getOrderStatus(String)},
     * SÍ propaga el error si el Team Service no responde: no hay una manera segura
     * de inscribir un equipo sin poder validar su roster.
     */
    TeamInfo getTeamInfo(String teamId);

    record TeamInfo(String teamName, int rosterSize) {}
}
