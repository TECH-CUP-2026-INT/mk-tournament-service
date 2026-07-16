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
import java.util.UUID;

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

    private static final UUID SCHEDULED_MATCH_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID MATCHUP_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID COURT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID REFEREE_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID HOME_TEAM_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID AWAY_TEAM_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");

    @Test
    void schedule_devuelve201() throws Exception {
        ScheduledMatch scheduled = ScheduledMatch.reconstruct(SCHEDULED_MATCH_ID, MATCHUP_ID, COURT_ID, REFEREE_ID,
                LocalDate.of(2026, 8, 5), LocalTime.of(9, 0));
        when(scheduleMatchUseCase.schedule(any())).thenReturn(scheduled);
        when(mapper.toResponse(any())).thenReturn(new ScheduledMatchResponse(
                SCHEDULED_MATCH_ID, MATCHUP_ID, COURT_ID, REFEREE_ID, LocalDate.of(2026, 8, 5), LocalTime.of(9, 0)));

        String body = """
                {"matchupId":"%s","matchDate":"2026-08-05","matchTime":"09:00:00","courtId":"%s","refereeId":"%s"}
                """.formatted(MATCHUP_ID, COURT_ID, REFEREE_ID);

        mockMvc.perform(post("/matches").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(SCHEDULED_MATCH_ID.toString()));
    }

    @Test
    void schedule_conConflictoDeHorario_devuelve409() throws Exception {
        when(scheduleMatchUseCase.schedule(any()))
                .thenThrow(new ScheduleConflictException(COURT_ID, REFEREE_ID));

        String body = """
                {"matchupId":"%s","matchDate":"2026-08-05","matchTime":"09:00:00","courtId":"%s","refereeId":"%s"}
                """.formatted(MATCHUP_ID, COURT_ID, REFEREE_ID);

        mockMvc.perform(post("/matches").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void activation_inactivar_devuelve200() throws Exception {
        Match match = new Match(MATCHUP_ID, HOME_TEAM_ID, AWAY_TEAM_ID, MatchStatus.PENDING);
        match.inactivate();
        when(inactivateMatchUseCase.execute(any())).thenReturn(match);

        mockMvc.perform(patch("/matches/" + MATCHUP_ID + "/activation")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"INACTIVATE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void activation_yaInactivo_devuelve409() throws Exception {
        when(inactivateMatchUseCase.execute(any()))
                .thenThrow(new MatchActivationNotAllowedException("El partido ya está inactivo"));

        mockMvc.perform(patch("/matches/" + MATCHUP_ID + "/activation")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"INACTIVATE\"}"))
                .andExpect(status().isConflict());
    }
}
