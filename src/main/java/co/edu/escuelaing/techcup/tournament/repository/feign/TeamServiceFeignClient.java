package co.edu.escuelaing.techcup.tournament.repository.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign hacia el Team Service. El contrato exacto de esa API aún no
 * está definido/versionado en ese repo; se asume el mismo shape que ya usaba
 * el cliente RestClient anterior (GET /teams/{teamId}).
 */
@FeignClient(name = "team-service", url = "${team-service.base-url}")
public interface TeamServiceFeignClient {

    @GetMapping("/teams/{teamId}")
    TeamInfoResponse getTeamInfo(@PathVariable("teamId") String teamId);

    record TeamInfoResponse(String teamName, int rosterSize) {}
}
