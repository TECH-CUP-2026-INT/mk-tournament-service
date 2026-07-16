package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.infrastructure.config.SecurityConfig;
import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.HistoricalTournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.EnrollmentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.HistoricalTournamentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.MatchupResponse;
import co.edu.escuelaing.techcup.tournament.application.mapper.EnrollmentRestMapper;
import co.edu.escuelaing.techcup.tournament.application.mapper.MatchupRestMapper;
import co.edu.escuelaing.techcup.tournament.application.mapper.TournamentRestMapper;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.domain.model.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.model.ParticipantStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.PreparationResult;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.AssignChampionUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RecordPenaltyShootoutWinnerUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.AttachRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.CheckTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ConsultHistoricalTournamentsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ConsultRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.CreateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.DeleteTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.DisqualifyTeamUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.EditTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.EnrollTeamInTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.FinalizeTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetChampionUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetEnrolledTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetTournamentByMatchUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.CheckTeamActiveEnrollmentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateTeamUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateUserUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.PauseTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RegisterCourtUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewCourtMapUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.StartTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewMatchCourtUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewMatchupsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewRegisteredTeamsUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TournamentController.class)
@Import(SecurityConfig.class)
class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean private CreateTournamentUseCase createTournamentUseCase;
    @MockitoBean private FinalizeTournamentUseCase finalizeTournamentUseCase;
    @MockitoBean private CheckTournamentPreparationUseCase checkPreparation;
    @MockitoBean private DeleteTournamentUseCase deleteTournamentUseCase;
    @MockitoBean private AssignChampionUseCase assignChampionUseCase;
    @MockitoBean private RecordPenaltyShootoutWinnerUseCase recordPenaltyShootoutWinnerUseCase;
    @MockitoBean private GetChampionUseCase getChampionUseCase;
    @MockitoBean private AttachRulebookUseCase attachRulebook;
    @MockitoBean private ConsultRulebookUseCase consultRulebook;
    @MockitoBean private RegisterCourtUseCase registerCourtUseCase;
    @MockitoBean private ViewCourtMapUseCase viewCourtMapUseCase;
    @MockitoBean private ConsultHistoricalTournamentsUseCase consultHistorical;
    @MockitoBean private GetEnrolledTeamsUseCase getEnrolledTeams;
    @MockitoBean private ViewRegisteredTeamsUseCase viewRegisteredTeams;
    @MockitoBean private EditTournamentUseCase editTournamentUseCase;
    @MockitoBean private PauseTournamentUseCase pauseTournamentUseCase;
    @MockitoBean private InactivateTournamentUseCase inactivateTournamentUseCase;
    @MockitoBean private DisqualifyTeamUseCase disqualifyTeamUseCase;
    @MockitoBean private InactivateTeamUseCase inactivateTeamUseCase;
    @MockitoBean private InactivateUserUseCase inactivateUserUseCase;
    @MockitoBean private EnrollTeamInTournamentUseCase enrollTeamInTournamentUseCase;
    @MockitoBean private StartTournamentPreparationUseCase startTournamentPreparation;
    @MockitoBean private ViewMatchupsUseCase viewMatchups;
    @MockitoBean private ViewMatchCourtUseCase viewMatchCourt;
    @MockitoBean private GetTournamentByMatchUseCase getTournamentByMatchUseCase;
    @MockitoBean private CheckTeamActiveEnrollmentUseCase checkTeamActiveEnrollmentUseCase;
    @MockitoBean private TournamentRestMapper mapper;
    @MockitoBean private MatchupRestMapper matchupRestMapper;
    @MockitoBean private EnrollmentRestMapper enrollmentRestMapper;

    private static final UUID TOURNAMENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID MISSING_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID MATCH_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID MATCH_ID_2 = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID COURT_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID COURT_ID_2 = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID SCHEDULED_MATCH_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");
    private static final UUID REFEREE_ID = UUID.fromString("77777777-7777-7777-7777-777777777777");
    private static final UUID HOME_TEAM_ID = UUID.fromString("88888888-8888-8888-8888-888888888888");
    private static final UUID AWAY_TEAM_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");
    private static final UUID TEAM_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID ENROLLMENT_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID USER_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    private Tournament sampleTournament(UUID id) {
        return Tournament.builder()
                .id(id).name("TechCup Fútbol 2026").type(TournamentType.NORMAL).format(TournamentFormat.BRACKETS)
                .numberOfTeams(8).cost(BigDecimal.valueOf(50000)).startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(20)).registrationDeadline(LocalDate.now().plusDays(5))
                .status(TournamentStatus.ACTIVE).teams(new ArrayList<>()).matches(new ArrayList<>())
                .paused(false)
                .reconstruct();
    }

    private co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.TournamentResponse sampleResponse(UUID id) {
        return new co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.TournamentResponse(
                id, "TechCup Fútbol 2026", TournamentType.NORMAL, TournamentFormat.BRACKETS, 8,
                BigDecimal.valueOf(50000), LocalDate.now().plusDays(10), LocalDate.now().plusDays(20),
                LocalDate.now().plusDays(5), null, null, TournamentStatus.ACTIVE, false, true);
    }

    private HistoricalTournamentResponse sampleHistoricalResponse(UUID id) {
        return new HistoricalTournamentResponse(
                id, "TechCup Fútbol 2026", 8, BigDecimal.valueOf(50000),
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(20),
                LocalDate.now().plusDays(5), TournamentStatus.ACTIVE, null);
    }

    @Test
    void create_datosValidos_devuelve201() throws Exception {
        when(mapper.toResponse(any())).thenReturn(sampleResponse(TOURNAMENT_ID));
        when(createTournamentUseCase.create(any())).thenReturn(sampleTournament(TOURNAMENT_ID));

        String body = """
                {"name":"TechCup Fútbol 2026","type":"NORMAL","format":"BRACKETS","numberOfTeams":8,
                "cost":50000,"startDate":"2026-08-01","endDate":"2026-08-31","registrationDeadline":"2026-07-25"}
                """;

        mockMvc.perform(post("/tournaments").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TOURNAMENT_ID.toString()));
    }

    @Test
    void create_datosInvalidos_devuelve400() throws Exception {
        String body = """
                {"name":"","type":"NORMAL","format":"BRACKETS","numberOfTeams":1,
                "cost":50000,"startDate":"2026-08-01","registrationDeadline":"2026-07-25"}
                """;

        mockMvc.perform(post("/tournaments").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_conVariosCamposInvalidos_devuelveTodosLosErroresNoSoloElPrimero() throws Exception {
        String body = """
                {"name":"","type":"NORMAL","format":"BRACKETS","numberOfTeams":1,
                "cost":50000,"startDate":"2026-08-01","registrationDeadline":"2026-07-25"}
                """;

        mockMvc.perform(post("/tournaments").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Datos de entrada inválidos"))
                .andExpect(jsonPath("$.details", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.details", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.containsString("name"))))
                .andExpect(jsonPath("$.details", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.containsString("numberOfTeams"))));
    }

    @Test
    void edit_devuelve200() throws Exception {
        when(editTournamentUseCase.edit(any())).thenReturn(sampleTournament(TOURNAMENT_ID));
        when(mapper.toResponse(any())).thenReturn(sampleResponse(TOURNAMENT_ID));

        mockMvc.perform(patch("/tournaments/" + TOURNAMENT_ID).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TOURNAMENT_ID.toString()));
    }

    @Test
    void finalize_devuelve200() throws Exception {
        when(finalizeTournamentUseCase.finalizeTournament(TOURNAMENT_ID)).thenReturn(sampleTournament(TOURNAMENT_ID));
        when(mapper.toResponse(any())).thenReturn(sampleResponse(TOURNAMENT_ID));

        mockMvc.perform(patch("/tournaments/" + TOURNAMENT_ID + "/finalize"))
                .andExpect(status().isOk());
    }

    @Test
    void finalize_cuandoNoExiste_devuelve404() throws Exception {
        when(finalizeTournamentUseCase.finalizeTournament(MISSING_ID))
                .thenThrow(new TournamentNotFoundException("No existe el torneo 'missing'"));

        mockMvc.perform(patch("/tournaments/" + MISSING_ID + "/finalize"))
                .andExpect(status().isNotFound());
    }

    @Test
    void prepare_devuelve200() throws Exception {
        when(startTournamentPreparation.startPreparation(TOURNAMENT_ID)).thenReturn(sampleTournament(TOURNAMENT_ID));
        when(mapper.toResponse(any())).thenReturn(sampleResponse(TOURNAMENT_ID));

        mockMvc.perform(patch("/tournaments/" + TOURNAMENT_ID + "/prepare"))
                .andExpect(status().isOk());
    }

    @Test
    void checkPreparation_devuelve200() throws Exception {
        when(checkPreparation.check(TOURNAMENT_ID)).thenReturn(new PreparationResult(true, List.of(), 8));

        mockMvc.perform(get("/tournaments/" + TOURNAMENT_ID + "/preparation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readyToActivate").value(true))
                .andExpect(jsonPath("$.approvedTeamsCount").value(8));
    }

    @Test
    void pause_devuelve200() throws Exception {
        when(pauseTournamentUseCase.execute(any())).thenReturn(sampleTournament(TOURNAMENT_ID));

        mockMvc.perform(patch("/tournaments/" + TOURNAMENT_ID + "/pause")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"PAUSE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tournamentId").value(TOURNAMENT_ID.toString()));
    }

    @Test
    void inactivate_devuelve200() throws Exception {
        when(inactivateTournamentUseCase.execute(any())).thenReturn(sampleTournament(TOURNAMENT_ID));

        mockMvc.perform(patch("/tournaments/" + TOURNAMENT_ID + "/inactivate")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"INACTIVATE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tournamentId").value(TOURNAMENT_ID.toString()));
    }

    @Test
    void delete_devuelve200() throws Exception {
        mockMvc.perform(delete("/tournaments/" + TOURNAMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getMatchups_devuelve200ConLista() throws Exception {
        when(viewMatchups.getMatchups(TOURNAMENT_ID)).thenReturn(List.of(
                new Match(MATCH_ID, HOME_TEAM_ID, AWAY_TEAM_ID, MatchStatus.PENDING)));
        when(matchupRestMapper.toResponse(any())).thenReturn(new MatchupResponse(
                MATCH_ID, HOME_TEAM_ID, AWAY_TEAM_ID, MatchStatus.PENDING, 0, 0, false));

        mockMvc.perform(get("/tournaments/" + TOURNAMENT_ID + "/matchups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matchId").value(MATCH_ID.toString()));
    }

    @Test
    void getMatchCourt_cuandoExisteCancha_devuelve200() throws Exception {
        Court court = Court.reconstruct(COURT_ID, TOURNAMENT_ID, CourtSection.CANCHA_1, "Descripción", "img1", MATCH_ID);
        when(viewMatchCourt.getCourtByMatch(MATCH_ID)).thenReturn(Optional.of(court));

        mockMvc.perform(get("/tournaments/matches/" + MATCH_ID + "/court"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courtId").value(COURT_ID.toString()));
    }

    @Test
    void getMatchCourt_cuandoNoHayCanchaAsignada_devuelvePendiente() throws Exception {
        when(viewMatchCourt.getCourtByMatch(MATCH_ID_2)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tournaments/matches/" + MATCH_ID_2 + "/court"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getByMatch_devuelve200() throws Exception {
        when(getTournamentByMatchUseCase.getByMatch(MATCH_ID)).thenReturn(sampleTournament(TOURNAMENT_ID));
        when(mapper.toResponse(any())).thenReturn(sampleResponse(TOURNAMENT_ID));

        mockMvc.perform(get("/tournaments/by-match/" + MATCH_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TOURNAMENT_ID.toString()));
    }

    @Test
    void getByMatch_cuandoNoExiste_devuelve404() throws Exception {
        when(getTournamentByMatchUseCase.getByMatch(MISSING_ID))
                .thenThrow(new MatchupNotFoundException(MISSING_ID));

        mockMvc.perform(get("/tournaments/by-match/" + MISSING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void hasActiveEnrollment_devuelveTrue() throws Exception {
        when(checkTeamActiveEnrollmentUseCase.hasActiveEnrollment(TEAM_ID)).thenReturn(true);

        mockMvc.perform(get("/tournaments/by-team/" + TEAM_ID + "/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId").value(TEAM_ID.toString()))
                .andExpect(jsonPath("$.hasActiveEnrollment").value(true));
    }

    @Test
    void hasActiveEnrollment_devuelveFalse() throws Exception {
        when(checkTeamActiveEnrollmentUseCase.hasActiveEnrollment(TEAM_ID)).thenReturn(false);

        mockMvc.perform(get("/tournaments/by-team/" + TEAM_ID + "/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasActiveEnrollment").value(false));
    }

    @Test
    void getCourtMap_devuelve200ConCanchaDisponibleYConPartido() throws Exception {
        Court available = Court.reconstruct(COURT_ID, TOURNAMENT_ID, CourtSection.CANCHA_1, "Court A", null, null);
        Court withMatch = Court.reconstruct(COURT_ID_2, TOURNAMENT_ID, CourtSection.CANCHA_2, "Court B", null, MATCH_ID);
        Match match = new Match(MATCH_ID, HOME_TEAM_ID, AWAY_TEAM_ID, MatchStatus.IN_PROGRESS);
        ScheduledMatch scheduledMatch = ScheduledMatch.reconstruct(
                SCHEDULED_MATCH_ID, MATCH_ID, COURT_ID_2, REFEREE_ID, LocalDate.of(2026, Month.AUGUST, 5), java.time.LocalTime.of(9, 0));

        when(viewCourtMapUseCase.getCourtMap(TOURNAMENT_ID)).thenReturn(List.of(
                new ViewCourtMapUseCase.CourtMapEntry(available, null, null),
                new ViewCourtMapUseCase.CourtMapEntry(withMatch, match, scheduledMatch)
        ));

        mockMvc.perform(get("/tournaments/" + TOURNAMENT_ID + "/courts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courtId").value(COURT_ID.toString()))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$[0].statusLabel").value("Available"))
                .andExpect(jsonPath("$[1].courtId").value(COURT_ID_2.toString()))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[1].statusLabel").value("In Progress"))
                .andExpect(jsonPath("$[1].matchId").value(MATCH_ID.toString()))
                .andExpect(jsonPath("$[1].matchDate").value("2026-08-05"));
    }

    @Test
    void getRegisteredTeams_devuelve200() throws Exception {
        when(viewRegisteredTeams.getTeams(TOURNAMENT_ID)).thenReturn(List.of(
                new TeamRegistration(TEAM_ID, "Los Compiladores", RegistrationStatus.APPROVED)));

        mockMvc.perform(get("/tournaments/" + TOURNAMENT_ID + "/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value(TEAM_ID.toString()));
    }

    @Test
    void getEnrolledTeams_devuelve200() throws Exception {
        Enrollment enrolled = new Enrollment(ENROLLMENT_ID, TEAM_ID, "Los Compiladores", EnrollmentStatus.ENROLLED, null, null);
        when(getEnrolledTeams.getEnrolledTeams(TOURNAMENT_ID))
                .thenReturn(new GetEnrolledTeamsUseCase.EnrolledTeamsView(List.of(enrolled), List.of(), 4));

        mockMvc.perform(get("/tournaments/" + TOURNAMENT_ID + "/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEnrolled").value(1))
                .andExpect(jsonPath("$.availableSlots").value(4));
    }

    @Test
    void enrollTeam_devuelve201() throws Exception {
        Enrollment enrollment = new Enrollment(ENROLLMENT_ID, TEAM_ID, "Los Compiladores", EnrollmentStatus.PENDING_PAYMENT, null, null);
        when(enrollTeamInTournamentUseCase.enrollTeam(TOURNAMENT_ID, TEAM_ID)).thenReturn(enrollment);
        when(enrollmentRestMapper.toResponse(any())).thenReturn(new EnrollmentResponse(
                ENROLLMENT_ID, EnrollmentStatus.PENDING_PAYMENT, null));

        mockMvc.perform(post("/tournaments/" + TOURNAMENT_ID + "/enrollments")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"teamId\":\"" + TEAM_ID + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enrollmentId").value(ENROLLMENT_ID.toString()));
    }

    @Test
    void getHistory_devuelve200() throws Exception {
        when(consultHistorical.findAll()).thenReturn(List.of(sampleTournament(TOURNAMENT_ID)));
        when(mapper.toHistoricalResponse(any())).thenReturn(sampleHistoricalResponse(TOURNAMENT_ID));

        mockMvc.perform(get("/tournaments/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TOURNAMENT_ID.toString()));
    }

    @Test
    void getHistoricalById_devuelve200() throws Exception {
        when(consultHistorical.findById(TOURNAMENT_ID)).thenReturn(sampleTournament(TOURNAMENT_ID));
        when(mapper.toHistoricalResponse(any())).thenReturn(sampleHistoricalResponse(TOURNAMENT_ID));

        mockMvc.perform(get("/tournaments/history/" + TOURNAMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TOURNAMENT_ID.toString()));
    }

    @Test
    void getHistoricalById_cuandoNoExiste_devuelve404() throws Exception {
        when(consultHistorical.findById(MISSING_ID)).thenThrow(new HistoricalTournamentNotFoundException(MISSING_ID));

        mockMvc.perform(get("/tournaments/history/" + MISSING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void consultRulebook_devuelvePdf() throws Exception {
        when(consultRulebook.consult(TOURNAMENT_ID)).thenReturn(new ConsultRulebookUseCase.RulebookResource(
                "reglamento.pdf", "application/pdf", new ByteArrayInputStream("pdf-content".getBytes())));

        mockMvc.perform(get("/tournaments/" + TOURNAMENT_ID + "/rulebook"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"reglamento.pdf\""));
    }

    @Test
    void attachRulebook_devuelve200() throws Exception {
        when(attachRulebook.attach(any())).thenReturn(sampleTournament(TOURNAMENT_ID));
        MockMultipartFile file = new MockMultipartFile("file", "reglamento.pdf",
                MediaType.APPLICATION_PDF_VALUE, "pdf-content".getBytes());

        mockMvc.perform(multipart("/tournaments/" + TOURNAMENT_ID + "/rulebook").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tournamentId").value(TOURNAMENT_ID.toString()));
    }

    @Test
    void registerCourt_devuelve201() throws Exception {
        Court court = Court.create(TOURNAMENT_ID, CourtSection.CANCHA_1, "Descripción");
        when(registerCourtUseCase.register(any())).thenReturn(court);

        mockMvc.perform(multipart("/tournaments/" + TOURNAMENT_ID + "/courts")
                        .param("section", "CANCHA_1")
                        .param("description", "Descripción"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tournamentId").value(TOURNAMENT_ID.toString()));
    }

    @Test
    void registerCourt_conImagen_devuelve201() throws Exception {
        Court court = Court.create(TOURNAMENT_ID, CourtSection.CANCHA_1, "Descripción");
        when(registerCourtUseCase.register(any())).thenReturn(court);
        MockMultipartFile image = new MockMultipartFile("image", "cancha.jpg",
                MediaType.IMAGE_JPEG_VALUE, "img-bytes".getBytes());

        mockMvc.perform(multipart("/tournaments/" + TOURNAMENT_ID + "/courts")
                        .file(image)
                        .param("section", "CANCHA_1")
                        .param("description", "Descripción"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tournamentId").value(TOURNAMENT_ID.toString()));
    }

    @Test
    void registerCourt_seccionInvalida_devuelve400() throws Exception {
        mockMvc.perform(multipart("/tournaments/" + TOURNAMENT_ID + "/courts")
                        .param("section", "CANCHA_9"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assignChampion_devuelve200() throws Exception {
        when(assignChampionUseCase.assignChampion(TOURNAMENT_ID, MATCH_ID))
                .thenReturn(new ChampionAssignment(TEAM_ID, ChampionResolution.REGULATION_TIME));

        mockMvc.perform(post("/tournaments/" + TOURNAMENT_ID + "/matches/" + MATCH_ID + "/champion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.championTeamId").value(TEAM_ID.toString()));
    }

    @Test
    void assignChampion_cuandoPartidoNoExiste_devuelve404() throws Exception {
        when(assignChampionUseCase.assignChampion(TOURNAMENT_ID, MISSING_ID))
                .thenThrow(new MatchNotFoundException(TOURNAMENT_ID, MISSING_ID));

        mockMvc.perform(post("/tournaments/" + TOURNAMENT_ID + "/matches/" + MISSING_ID + "/champion"))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignChampion_cuandoNoPermitido_devuelve409() throws Exception {
        when(assignChampionUseCase.assignChampion(TOURNAMENT_ID, MATCH_ID))
                .thenThrow(new ChampionAssignmentNotAllowedException("El partido debe estar finalizado"));

        mockMvc.perform(post("/tournaments/" + TOURNAMENT_ID + "/matches/" + MATCH_ID + "/champion"))
                .andExpect(status().isConflict());
    }

    @Test
    void recordPenaltyShootoutWinner_devuelve200() throws Exception {
        mockMvc.perform(post("/tournaments/" + TOURNAMENT_ID + "/matches/" + MATCH_ID + "/penalty-shootout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"winnerTeamId\":\"" + TEAM_ID + "\"}"))
                .andExpect(status().isOk());

        verify(recordPenaltyShootoutWinnerUseCase).recordWinner(
                new RecordPenaltyShootoutWinnerUseCase.RecordPenaltyShootoutWinnerCommand(TOURNAMENT_ID, MATCH_ID, TEAM_ID));
    }

    @Test
    void recordPenaltyShootoutWinner_cuandoNoEmpatado_devuelve409() throws Exception {
        doThrow(new ChampionAssignmentNotAllowedException(
                        "La tanda de penales solo aplica cuando hay empate en tiempo reglamentario"))
                .when(recordPenaltyShootoutWinnerUseCase).recordWinner(org.mockito.ArgumentMatchers.any());

        mockMvc.perform(post("/tournaments/" + TOURNAMENT_ID + "/matches/" + MATCH_ID + "/penalty-shootout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"winnerTeamId\":\"" + TEAM_ID + "\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void recordPenaltyShootoutWinner_datosInvalidos_devuelve400() throws Exception {
        mockMvc.perform(post("/tournaments/" + TOURNAMENT_ID + "/matches/" + MATCH_ID + "/penalty-shootout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"winnerTeamId\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getChampion_devuelve200() throws Exception {
        when(getChampionUseCase.getChampion(TOURNAMENT_ID))
                .thenReturn(new ChampionAssignment(TEAM_ID, ChampionResolution.PENALTIES));

        mockMvc.perform(get("/tournaments/" + TOURNAMENT_ID + "/champion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolution").value("PENALTIES"));
    }

    @Test
    void disqualifyTeam_devuelve200() throws Exception {
        mockMvc.perform(patch("/tournaments/" + TOURNAMENT_ID + "/teams/" + TEAM_ID + "/disqualify")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"RULES_VIOLATION\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(RegistrationStatus.DISQUALIFIED.name()));
    }

    @Test
    void inactivateTeam_devuelve200() throws Exception {
        mockMvc.perform(patch("/tournaments/" + TOURNAMENT_ID + "/teams/" + TEAM_ID + "/inactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(RegistrationStatus.INACTIVE.name()));
    }

    @Test
    void inactivateUser_devuelve200() throws Exception {
        mockMvc.perform(patch("/tournaments/" + TOURNAMENT_ID + "/users/" + USER_ID + "/inactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ParticipantStatus.INACTIVE.name()));
    }
}
