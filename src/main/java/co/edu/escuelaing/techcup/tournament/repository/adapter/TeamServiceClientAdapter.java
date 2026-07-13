package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.exception.TeamServiceUnavailableException;
import co.edu.escuelaing.techcup.tournament.service.ports.TeamServiceClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP hacia el Team Service (GET /teams/{teamId}). El contrato exacto
 * de esa API aún no está definido en este repo; mismo patrón de resiliencia
 * (timeout, RestClient) que {@link PaymentServiceClientAdapter}, pero a
 * diferencia de este, las fallas SÍ se propagan (ver {@link TeamServiceClientPort}).
 */
@Component
public class TeamServiceClientAdapter implements TeamServiceClientPort {

    private static final int TIMEOUT_MILLIS = 2500;

    private final RestClient restClient;

    public TeamServiceClientAdapter(
            @Value("${team-service.base-url:http://localhost:8082}") String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT_MILLIS);
        requestFactory.setReadTimeout(TIMEOUT_MILLIS);
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public TeamInfo getTeamInfo(String teamId) {
        try {
            TeamInfo response = restClient.get()
                    .uri("/teams/{teamId}", teamId)
                    .retrieve()
                    .body(TeamInfo.class);
            if (response == null) {
                throw new IllegalStateException("Respuesta vacía del Team Service");
            }
            return response;
        } catch (Exception ex) {
            throw new TeamServiceUnavailableException(teamId, ex);
        }
    }
}
