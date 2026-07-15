package co.edu.escuelaing.techcup.tournament.controller.impl;

import co.edu.escuelaing.techcup.tournament.config.SecurityConfig;
import co.edu.escuelaing.techcup.tournament.dto.response.SanctionResponse;
import co.edu.escuelaing.techcup.tournament.mapper.SanctionRestMapper;
import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.service.SanctionType;
import co.edu.escuelaing.techcup.tournament.service.ports.ApplySanctionUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.RecordMatchFinishedForSanctionsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewPlayerSanctionUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SanctionController.class)
@Import(SecurityConfig.class)
class SanctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private ApplySanctionUseCase applySanctionUseCase;
    @MockitoBean private ViewPlayerSanctionUseCase viewPlayerSanctionUseCase;
    @MockitoBean private RecordMatchFinishedForSanctionsUseCase recordMatchFinishedUseCase;
    @MockitoBean private SanctionRestMapper mapper;

    @Test
    void apply_devuelve201() throws Exception {
        PlayerSanction sanction = PlayerSanction.reconstruct("s1", "player1", SanctionType.RED_CARD, 1);
        when(applySanctionUseCase.apply(any())).thenReturn(sanction);
        when(mapper.toResponse(any())).thenReturn(new SanctionResponse(
                "s1", "player1", SanctionType.RED_CARD, 1, true));

        String body = """
                {"playerId":"player1","type":"RED_CARD"}
                """;

        mockMvc.perform(post("/sanctions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("s1"))
                .andExpect(jsonPath("$.matchesRemaining").value(1));
    }

    @Test
    void apply_datosInvalidos_devuelve400() throws Exception {
        String body = """
                {"playerId":"","type":"RED_CARD"}
                """;

        mockMvc.perform(post("/sanctions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getActiveSanctions_devuelve200() throws Exception {
        PlayerSanction sanction = PlayerSanction.reconstruct("s1", "player1", SanctionType.YELLOW_CARD_ACCUMULATION, 1);
        when(viewPlayerSanctionUseCase.getActiveSanctions("player1")).thenReturn(List.of(sanction));
        when(mapper.toResponse(any())).thenReturn(new SanctionResponse(
                "s1", "player1", SanctionType.YELLOW_CARD_ACCUMULATION, 1, true));

        mockMvc.perform(get("/sanctions/player1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerId").value("player1"));
    }

    @Test
    void recordMatchFinished_devuelve200() throws Exception {
        mockMvc.perform(post("/sanctions/match-finished"))
                .andExpect(status().isOk());

        verify(recordMatchFinishedUseCase).recordMatchFinished();
    }
}
