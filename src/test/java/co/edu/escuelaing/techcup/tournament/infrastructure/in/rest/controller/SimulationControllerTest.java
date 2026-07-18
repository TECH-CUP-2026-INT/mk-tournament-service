package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetTournamentByMatchUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ProcessMatchResultUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ProcessMatchResultUseCase.ProcessMatchResultCommand;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;
import co.edu.escuelaing.techcup.tournament.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SimulationController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("dev")
class SimulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private ProcessMatchResultUseCase processMatchResult;
    @MockitoBean private GetTournamentByMatchUseCase getTournamentByMatch;

    private static final UUID MATCH_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TOURNAMENT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID WINNER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private Tournament sampleTournament() {
        return Tournament.builder()
                .id(TOURNAMENT_ID).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(TournamentStatus.IN_PROGRESS).teams(List.of()).matches(List.of())
                .reconstruct();
    }

    @Test
    void simulateResult_resuelveElTorneoPorMatchIdYLlamaAlUseCase() throws Exception {
        when(getTournamentByMatch.getByMatch(MATCH_ID)).thenReturn(sampleTournament());

        String body = """
                {"golesA":2,"golesB":1,"ganadorId":"%s","eliminadoId":null,"fase":"GRUPOS"}
                """.formatted(WINNER_ID);

        mockMvc.perform(post("/sim/partidos/" + MATCH_ID + "/resultado")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        verify(processMatchResult).process(new ProcessMatchResultCommand(
                MATCH_ID, TOURNAMENT_ID, MatchPhase.GRUPOS, 2, 1, WINNER_ID, null));
    }

    @Test
    void simulateResult_empatadoSinGanador_pasaGanadorNull() throws Exception {
        when(getTournamentByMatch.getByMatch(MATCH_ID)).thenReturn(sampleTournament());

        String body = """
                {"golesA":1,"golesB":1,"fase":"ELIMINATORIA"}
                """;

        mockMvc.perform(post("/sim/partidos/" + MATCH_ID + "/resultado")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        verify(processMatchResult).process(new ProcessMatchResultCommand(
                MATCH_ID, TOURNAMENT_ID, MatchPhase.ELIMINATORIA, 1, 1, null, null));
    }

    @Test
    void simulateResult_walkover_pasaAusenteIdAlComando() throws Exception {
        when(getTournamentByMatch.getByMatch(MATCH_ID)).thenReturn(sampleTournament());
        UUID absentTeamId = UUID.fromString("44444444-4444-4444-4444-444444444444");

        String body = """
                {"golesA":0,"golesB":0,"ganadorId":"%s","ausenteId":"%s","fase":"GRUPOS"}
                """.formatted(WINNER_ID, absentTeamId);

        mockMvc.perform(post("/sim/partidos/" + MATCH_ID + "/resultado")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        verify(processMatchResult).process(new ProcessMatchResultCommand(
                MATCH_ID, TOURNAMENT_ID, MatchPhase.GRUPOS, 0, 0, WINNER_ID, absentTeamId));
    }

    @Test
    void simulateResult_matchNoExiste_devuelve404() throws Exception {
        when(getTournamentByMatch.getByMatch(MATCH_ID)).thenThrow(new MatchupNotFoundException(MATCH_ID));

        String body = """
                {"golesA":1,"golesB":0,"fase":"GRUPOS"}
                """;

        mockMvc.perform(post("/sim/partidos/" + MATCH_ID + "/resultado")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void simulateResult_datosInvalidos_devuelve400() throws Exception {
        String body = """
                {"golesA":null,"golesB":0,"fase":"GRUPOS"}
                """;

        mockMvc.perform(post("/sim/partidos/" + MATCH_ID + "/resultado")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
