package co.edu.escuelaing.techcup.tournament.infrastructure.out.feign;

import co.edu.escuelaing.techcup.tournament.domain.exception.MatchDefinitionPushFailedException;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.MatchDefinitionPort.MatchDefinition;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MatchDefinitionRestAdapterTest {

    private static final String API_KEY = "secret-internal-key";

    private final MatchesServiceFeignClient feignClient = mock(MatchesServiceFeignClient.class);
    private final MatchDefinitionRestAdapter adapter = new MatchDefinitionRestAdapter(feignClient, API_KEY);

    private final UUID matchId = UUID.randomUUID();
    private final UUID tournamentId = UUID.randomUUID();
    private final UUID homeTeamId = UUID.randomUUID();
    private final UUID awayTeamId = UUID.randomUUID();
    private final UUID refereeId = UUID.randomUUID();
    private final UUID courtId = UUID.randomUUID();

    private MatchDefinition sampleDefinition() {
        return new MatchDefinition(
                matchId, tournamentId, MatchPhase.GRUPOS, homeTeamId, awayTeamId,
                "Los Tigres", "Los Leones",
                LocalDate.of(2026, Month.AUGUST, 1), LocalTime.of(15, 0), refereeId, courtId);
    }

    @Test
    void sendDefinition_mapeaElPayloadEnEspanolYEnviaElHeaderDeApiKey() {
        adapter.sendDefinition(sampleDefinition());

        MatchesServiceFeignClient.MatchDefinitionRequest expected = new MatchesServiceFeignClient.MatchDefinitionRequest(
                matchId, tournamentId, "GRUPOS", homeTeamId, awayTeamId, "Los Tigres", "Los Leones",
                LocalDate.of(2026, Month.AUGUST, 1), LocalTime.of(15, 0), refereeId, courtId);

        verify(feignClient).createOrUpdateMatch(API_KEY, expected);
    }

    @Test
    void sendDefinition_faseNull_seEnviaComoNullSinExplotar() {
        MatchDefinition definition = new MatchDefinition(
                matchId, tournamentId, null, homeTeamId, awayTeamId, "Los Tigres", "Los Leones",
                LocalDate.of(2026, Month.AUGUST, 1), LocalTime.of(15, 0), refereeId, courtId);

        adapter.sendDefinition(definition);

        verify(feignClient).createOrUpdateMatch(eq(API_KEY), any());
    }

    @Test
    void sendDefinition_409YaNoEditable_seIgnoraComoCasoEsperado() {
        when(feignClient.createOrUpdateMatch(any(), any())).thenThrow(conflictException());

        assertDoesNotThrow(() -> adapter.sendDefinition(sampleDefinition()));
    }

    @Test
    void sendDefinition_otraFalla_lanzaMatchDefinitionPushFailedException() {
        when(feignClient.createOrUpdateMatch(any(), any())).thenThrow(new RuntimeException("timeout"));
        MatchDefinition definition = sampleDefinition();

        assertThrows(MatchDefinitionPushFailedException.class, () -> adapter.sendDefinition(definition));
    }

    @Test
    void notifyMatchInactivated_noLlamaAlFeignClientNiLanza() {
        assertDoesNotThrow(() -> adapter.notifyMatchInactivated(matchId));

        verifyNoInteractions(feignClient);
    }

    private FeignException conflictException() {
        Request request = Request.create(Request.HttpMethod.POST, "/api/partidos", Map.of(),
                null, StandardCharsets.UTF_8, null);
        Response response = Response.builder()
                .status(409)
                .reason("Conflict")
                .request(request)
                .headers(Map.of())
                .build();
        return FeignException.errorStatus("MatchesServiceFeignClient#createOrUpdateMatch", response);
    }
}
