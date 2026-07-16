package co.edu.escuelaing.techcup.tournament.infrastructure.out.feign;

import co.edu.escuelaing.techcup.tournament.domain.exception.TeamServiceUnavailableException;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TeamServiceClientPort;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TeamServiceClientAdapterTest {

    private final UUID teamId = UUID.randomUUID();

    @Test
    void getTeamInfo_cuandoElFeignClientResponde_retornaLaInfo() {
        TeamServiceFeignClient feignClient = mock(TeamServiceFeignClient.class);
        when(feignClient.getTeamInfo(teamId))
                .thenReturn(new TeamServiceFeignClient.TeamInfoResponse("Los Compiladores", 9));

        TeamServiceClientAdapter adapter = new TeamServiceClientAdapter(feignClient);

        TeamServiceClientPort.TeamInfo result = adapter.getTeamInfo(teamId);

        assertEquals("Los Compiladores", result.teamName());
        assertEquals(9, result.rosterSize());
    }

    @Test
    void getTeamInfo_cuandoElFeignClientFalla_lanzaTeamServiceUnavailableException() {
        TeamServiceFeignClient feignClient = mock(TeamServiceFeignClient.class);
        when(feignClient.getTeamInfo(teamId)).thenThrow(new RuntimeException("connection refused"));

        TeamServiceClientAdapter adapter = new TeamServiceClientAdapter(feignClient);

        assertThrows(TeamServiceUnavailableException.class, () -> adapter.getTeamInfo(teamId));
    }
}
