package co.edu.escuelaing.techcup.tournament.infrastructure.out.feign;

import co.edu.escuelaing.techcup.tournament.domain.exception.TeamServiceUnavailableException;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.feign.TeamServiceFeignClient;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TeamServiceClientPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
    public TeamInfo getTeamInfo(UUID teamId) {
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
