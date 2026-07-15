package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.infrastructure.config.SecurityConfig;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.ScheduledMatchResponse;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchActivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.ScheduleConflictException;
import co.edu.escuelaing.techcup.tournament.application.mapper.ScheduledMatchRestMapper;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateMatchUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ScheduleMatchUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchController.class)
@Import(SecurityConfig.class)
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private ScheduleMatchUseCase scheduleMatchUseCase;
    @MockitoBean private InactivateMatchUseCase inactivateMatchUseCase;
    @MockitoBean private ScheduledMatchRestMapper mapper;

    @Test
    void schedule_devuelve201() throws Exception {
        ScheduledMatch scheduled = ScheduledMatch.reconstruct("sm1", "m01", "court-1", "ref-1",
                LocalDate.of(2026, 8, 5), LocalTime.of(9, 0));
        when(scheduleMatchUseCase.schedule(any())).thenReturn(scheduled);
        when(mapper.toResponse(any())).thenReturn(new ScheduledMatchResponse(
                "sm1", "m01", "court-1", "ref-1", LocalDate.of(2026, 8, 5), LocalTime.of(9, 0)));

        String body = """
                {"matchupId":"m01","matchDate":"2026-08-05","matchTime":"09:00:00","courtId":"court-1","refereeId":"ref-1"}
                """;

        mockMvc.perform(post("/matches").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("sm1"));
    }

    @Test
    void schedule_conConflictoDeHorario_devuelve409() throws Exception {
        when(scheduleMatchUseCase.schedule(any()))
                .thenThrow(new ScheduleConflictException("court-1", "ref-1"));

        String body = """
                {"matchupId":"m01","matchDate":"2026-08-05","matchTime":"09:00:00","courtId":"court-1","refereeId":"ref-1"}
                """;

        mockMvc.perform(post("/matches").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void activation_inactivar_devuelve200() throws Exception {
        Match match = new Match("m01", "home", "away", MatchStatus.PENDING);
        match.inactivate();
        when(inactivateMatchUseCase.execute(any())).thenReturn(match);

        mockMvc.perform(patch("/matches/m01/activation")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"INACTIVATE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void activation_yaInactivo_devuelve409() throws Exception {
        when(inactivateMatchUseCase.execute(any()))
                .thenThrow(new MatchActivationNotAllowedException("El partido ya está inactivo"));

        mockMvc.perform(patch("/matches/m01/activation")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"INACTIVATE\"}"))
                .andExpect(status().isConflict());
    }
}
