package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.infrastructure.config.SecurityConfig;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.SanctionResponse;
import co.edu.escuelaing.techcup.tournament.application.mapper.SanctionRestMapper;
import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ApplySanctionUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RecordMatchFinishedForSanctionsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewPlayerSanctionUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

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

    private static final UUID SANCTION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID PLAYER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Test
    void apply_devuelve201() throws Exception {
        PlayerSanction sanction = PlayerSanction.reconstruct(SANCTION_ID, PLAYER_ID, SanctionType.RED_CARD, 1);
        when(applySanctionUseCase.apply(any())).thenReturn(sanction);
        when(mapper.toResponse(any())).thenReturn(new SanctionResponse(
                SANCTION_ID, PLAYER_ID, SanctionType.RED_CARD, 1, true));

        String body = """
                {"playerId":"%s","type":"RED_CARD"}
                """.formatted(PLAYER_ID);

        mockMvc.perform(post("/sanctions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(SANCTION_ID.toString()))
                .andExpect(jsonPath("$.matchesRemaining").value(1));
    }

    @Test
    void apply_datosInvalidos_devuelve400() throws Exception {
        String body = """
                {"playerId":null,"type":"RED_CARD"}
                """;

        mockMvc.perform(post("/sanctions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getActiveSanctions_devuelve200() throws Exception {
        PlayerSanction sanction = PlayerSanction.reconstruct(SANCTION_ID, PLAYER_ID, SanctionType.YELLOW_CARD_ACCUMULATION, 1);
        when(viewPlayerSanctionUseCase.getActiveSanctions(PLAYER_ID)).thenReturn(List.of(sanction));
        when(mapper.toResponse(any())).thenReturn(new SanctionResponse(
                SANCTION_ID, PLAYER_ID, SanctionType.YELLOW_CARD_ACCUMULATION, 1, true));

        mockMvc.perform(get("/sanctions/" + PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerId").value(PLAYER_ID.toString()));
    }

    @Test
    void recordMatchFinished_devuelve200() throws Exception {
        mockMvc.perform(post("/sanctions/match-finished"))
                .andExpect(status().isOk());

        verify(recordMatchFinishedUseCase).recordMatchFinished();
    }
}
