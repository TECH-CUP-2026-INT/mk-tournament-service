package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.exception.TeamServiceUnavailableException;
import co.edu.escuelaing.techcup.tournament.repository.feign.TeamServiceFeignClient;
import co.edu.escuelaing.techcup.tournament.service.ports.TeamServiceClientPort;
import org.springframework.stereotype.Component;

/**
 * Cliente hacia el Team Service, vía Feign ({@link TeamServiceFeignClient}).
 * El contrato exacto de esa API aún no está definido en este repo; a
 * diferencia de {@link PaymentServiceClientAdapter}, las fallas SÍ se
 * propagan (ver {@link TeamServiceClientPort}).
 */
@Component
public class TeamServiceClientAdapter implements TeamServiceClientPort {

    private final TeamServiceFeignClient feignClient;

    public TeamServiceClientAdapter(TeamServiceFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    public TeamInfo getTeamInfo(String teamId) {
        try {
            TeamServiceFeignClient.TeamInfoResponse response = feignClient.getTeamInfo(teamId);
            if (response == null) {
                throw new IllegalStateException("Respuesta vacía del Team Service");
            }
            return new TeamInfo(response.teamName(), response.rosterSize());
        } catch (Exception ex) {
            throw new TeamServiceUnavailableException(teamId, ex);
        }
    }
}
