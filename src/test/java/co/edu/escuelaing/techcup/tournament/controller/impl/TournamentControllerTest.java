package co.edu.escuelaing.techcup.tournament.controller.impl;

import co.edu.escuelaing.techcup.tournament.config.SecurityConfig;
import co.edu.escuelaing.techcup.tournament.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.HistoricalTournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.exception.MatchNotFoundException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.mapper.TournamentRestMapper;
import co.edu.escuelaing.techcup.tournament.service.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.service.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.CourtSection;
import co.edu.escuelaing.techcup.tournament.service.DisqualificationReason;
import co.edu.escuelaing.techcup.tournament.service.Enrollment;
import co.edu.escuelaing.techcup.tournament.service.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.service.Match;
import co.edu.escuelaing.techcup.tournament.service.MatchStatus;
import co.edu.escuelaing.techcup.tournament.service.ParticipantStatus;
import co.edu.escuelaing.techcup.tournament.service.PaymentOrderStatus;
import co.edu.escuelaing.techcup.tournament.service.PreparationResult;
import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.service.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentParticipant;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.ports.AssignChampionUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.AttachRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.CheckTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ConsultHistoricalTournamentsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ConsultRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.CreateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.DeleteTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.DisqualifyTeamUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.EditTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.EnrollTeamInTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.FinalizeTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.GetChampionUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.GetEnrolledTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateTeamUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateUserUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.PauseTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.RegisterCourtUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.StartTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewMatchCourtUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewMatchupsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewRegisteredTeamsUseCase;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    @MockitoBean private GetChampionUseCase getChampionUseCase;
    @MockitoBean private AttachRulebookUseCase attachRulebook;
    @MockitoBean private ConsultRulebookUseCase consultRulebook;
    @MockitoBean private RegisterCourtUseCase registerCourtUseCase;
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
    @MockitoBean private TournamentRestMapper mapper;

    private Tournament sampleTournament(String id) {
        return Tournament.reconstruct(id, "TechCup Fútbol 2026", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                8, BigDecimal.valueOf(50000), LocalDate.now().plusDays(10), LocalDate.now().plusDays(20),
                LocalDate.now().plusDays(5), null, null, TournamentStatus.ACTIVE,
                new ArrayList<>(), new ArrayList<>(), null, null, false);
    }

    private co.edu.escuelaing.techcup.tournament.dto.response.TournamentResponse sampleResponse(String id) {
        return new co.edu.escuelaing.techcup.tournament.dto.response.TournamentResponse(
                id, "TechCup Fútbol 2026", TournamentType.NORMAL, TournamentFormat.BRACKETS, 8,
                BigDecimal.valueOf(50000), LocalDate.now().plusDays(10), LocalDate.now().plusDays(20),
                LocalDate.now().plusDays(5), null, null, TournamentStatus.ACTIVE, false, true);
    }

    @Test
    void create_datosValidos_devuelve201() throws Exception {
        when(mapper.toResponse(any())).thenReturn(sampleResponse("t1"));
        when(createTournamentUseCase.create(any())).thenReturn(sampleTournament("t1"));

        String body = """
                {"name":"TechCup Fútbol 2026","type":"NORMAL","format":"BRACKETS","numberOfTeams":8,
                "cost":50000,"startDate":"2026-08-01","endDate":"2026-08-31","registrationDeadline":"2026-07-25"}
                """;

        mockMvc.perform(post("/tournaments").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("t1"));
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
    void edit_devuelve200() throws Exception {
        when(editTournamentUseCase.edit(any())).thenReturn(sampleTournament("t1"));
        when(mapper.toResponse(any())).thenReturn(sampleResponse("t1"));

        mockMvc.perform(patch("/tournaments/t1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("t1"));
    }

    @Test
    void finalize_devuelve200() throws Exception {
        when(finalizeTournamentUseCase.finalizeTournament("t1")).thenReturn(sampleTournament("t1"));
        when(mapper.toResponse(any())).thenReturn(sampleResponse("t1"));

        mockMvc.perform(patch("/tournaments/t1/finalize"))
                .andExpect(status().isOk());
    }

    @Test
    void finalize_cuandoNoExiste_devuelve404() throws Exception {
        when(finalizeTournamentUseCase.finalizeTournament("missing"))
                .thenThrow(new TournamentNotFoundException("No existe el torneo 'missing'"));

        mockMvc.perform(patch("/tournaments/missing/finalize"))
                .andExpect(status().isNotFound());
    }

    @Test
    void prepare_devuelve200() throws Exception {
        when(startTournamentPreparation.startPreparation("t1")).thenReturn(sampleTournament("t1"));
        when(mapper.toResponse(any())).thenReturn(sampleResponse("t1"));

        mockMvc.perform(patch("/tournaments/t1/prepare"))
                .andExpect(status().isOk());
    }

    @Test
    void checkPreparation_devuelve200() throws Exception {
        when(checkPreparation.check("t1")).thenReturn(new PreparationResult(true, List.of(), 8));

        mockMvc.perform(get("/tournaments/t1/preparation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readyToActivate").value(true))
                .andExpect(jsonPath("$.approvedTeamsCount").value(8));
    }

    @Test
    void pause_devuelve200() throws Exception {
        when(pauseTournamentUseCase.execute(any())).thenReturn(sampleTournament("t1"));

        mockMvc.perform(patch("/tournaments/t1/pause")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"PAUSE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tournamentId").value("t1"));
    }

    @Test
    void inactivate_devuelve200() throws Exception {
        when(inactivateTournamentUseCase.execute(any())).thenReturn(sampleTournament("t1"));

        mockMvc.perform(patch("/tournaments/t1/inactivate")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"INACTIVATE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tournamentId").value("t1"));
    }

    @Test
    void delete_devuelve200() throws Exception {
        mockMvc.perform(delete("/tournaments/t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getMatchups_devuelve200ConLista() throws Exception {
        when(viewMatchups.getMatchups("t1")).thenReturn(List.of(
                new Match("m1", "home", "away", MatchStatus.PENDING)));

        mockMvc.perform(get("/tournaments/t1/matchups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matchId").value("m1"));
    }

    @Test
    void getMatchCourt_cuandoExisteCancha_devuelve200() throws Exception {
        Court court = Court.reconstruct("c1", "t1", CourtSection.CANCHA_1, "Descripción", "img1", "m1");
        when(viewMatchCourt.getCourtByMatch("m1")).thenReturn(Optional.of(court));

        mockMvc.perform(get("/tournaments/matches/m1/court"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courtId").value("c1"));
    }

    @Test
    void getMatchCourt_cuandoNoHayCanchaAsignada_devuelvePendiente() throws Exception {
        when(viewMatchCourt.getCourtByMatch("m2")).thenReturn(Optional.empty());

        mockMvc.perform(get("/tournaments/matches/m2/court"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getRegisteredTeams_devuelve200() throws Exception {
        when(viewRegisteredTeams.getTeams("t1")).thenReturn(List.of(
                new TeamRegistration("team1", "Los Compiladores", RegistrationStatus.APPROVED)));

        mockMvc.perform(get("/tournaments/t1/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value("team1"));
    }

    @Test
    void getEnrolledTeams_devuelve200() throws Exception {
        Enrollment enrolled = new Enrollment("e1", "team1", "Los Compiladores", EnrollmentStatus.ENROLLED, null, null);
        when(getEnrolledTeams.getEnrolledTeams("t1"))
                .thenReturn(new GetEnrolledTeamsUseCase.EnrolledTeamsView(List.of(enrolled), List.of(), 4));

        mockMvc.perform(get("/tournaments/t1/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEnrolled").value(1))
                .andExpect(jsonPath("$.availableSlots").value(4));
    }

    @Test
    void enrollTeam_devuelve201() throws Exception {
        Enrollment enrollment = new Enrollment("e1", "team1", "Los Compiladores", EnrollmentStatus.PENDING_PAYMENT, null, null);
        when(enrollTeamInTournamentUseCase.enrollTeam("t1", "team1")).thenReturn(enrollment);

        mockMvc.perform(post("/tournaments/t1/enrollments")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"teamId\":\"team1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enrollmentId").value("e1"));
    }

    @Test
    void getHistory_devuelve200() throws Exception {
        when(consultHistorical.findAll()).thenReturn(List.of(sampleTournament("t1")));

        mockMvc.perform(get("/tournaments/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("t1"));
    }

    @Test
    void getHistoricalById_devuelve200() throws Exception {
        when(consultHistorical.findById("t1")).thenReturn(sampleTournament("t1"));

        mockMvc.perform(get("/tournaments/history/t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("t1"));
    }

    @Test
    void getHistoricalById_cuandoNoExiste_devuelve404() throws Exception {
        when(consultHistorical.findById("missing")).thenThrow(new HistoricalTournamentNotFoundException("missing"));

        mockMvc.perform(get("/tournaments/history/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void consultRulebook_devuelvePdf() throws Exception {
        when(consultRulebook.consult("t1")).thenReturn(new ConsultRulebookUseCase.RulebookResource(
                "reglamento.pdf", "application/pdf", new ByteArrayInputStream("pdf-content".getBytes())));

        mockMvc.perform(get("/tournaments/t1/rulebook"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"reglamento.pdf\""));
    }

    @Test
    void attachRulebook_devuelve200() throws Exception {
        when(attachRulebook.attach(any())).thenReturn(sampleTournament("t1"));
        MockMultipartFile file = new MockMultipartFile("file", "reglamento.pdf",
                MediaType.APPLICATION_PDF_VALUE, "pdf-content".getBytes());

        mockMvc.perform(multipart("/tournaments/t1/rulebook").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tournamentId").value("t1"));
    }

    @Test
    void registerCourt_devuelve201() throws Exception {
        Court court = Court.create("t1", CourtSection.CANCHA_1, "Descripción");
        when(registerCourtUseCase.register(any())).thenReturn(court);

        mockMvc.perform(multipart("/tournaments/t1/courts")
                        .param("section", "CANCHA_1")
                        .param("description", "Descripción"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tournamentId").value("t1"));
    }

    @Test
    void registerCourt_conImagen_devuelve201() throws Exception {
        Court court = Court.create("t1", CourtSection.CANCHA_1, "Descripción");
        when(registerCourtUseCase.register(any())).thenReturn(court);
        MockMultipartFile image = new MockMultipartFile("image", "cancha.jpg",
                MediaType.IMAGE_JPEG_VALUE, "img-bytes".getBytes());

        mockMvc.perform(multipart("/tournaments/t1/courts")
                        .file(image)
                        .param("section", "CANCHA_1")
                        .param("description", "Descripción"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tournamentId").value("t1"));
    }

    @Test
    void registerCourt_seccionInvalida_devuelve400() throws Exception {
        mockMvc.perform(multipart("/tournaments/t1/courts")
                        .param("section", "CANCHA_9"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assignChampion_devuelve200() throws Exception {
        when(assignChampionUseCase.assignChampion("t1", "m1"))
                .thenReturn(new ChampionAssignment("team1", ChampionResolution.REGULATION_TIME));

        mockMvc.perform(post("/tournaments/t1/matches/m1/champion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.championTeamId").value("team1"));
    }

    @Test
    void assignChampion_cuandoPartidoNoExiste_devuelve404() throws Exception {
        when(assignChampionUseCase.assignChampion("t1", "missing"))
                .thenThrow(new MatchNotFoundException("t1", "missing"));

        mockMvc.perform(post("/tournaments/t1/matches/missing/champion"))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignChampion_cuandoNoPermitido_devuelve409() throws Exception {
        when(assignChampionUseCase.assignChampion("t1", "m1"))
                .thenThrow(new ChampionAssignmentNotAllowedException("El partido debe estar finalizado"));

        mockMvc.perform(post("/tournaments/t1/matches/m1/champion"))
                .andExpect(status().isConflict());
    }

    @Test
    void getChampion_devuelve200() throws Exception {
        when(getChampionUseCase.getChampion("t1"))
                .thenReturn(new ChampionAssignment("team1", ChampionResolution.PENALTIES));

        mockMvc.perform(get("/tournaments/t1/champion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolution").value("PENALTIES"));
    }

    @Test
    void disqualifyTeam_devuelve200() throws Exception {
        mockMvc.perform(patch("/tournaments/t1/teams/team1/disqualify")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"RULES_VIOLATION\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(RegistrationStatus.DISQUALIFIED.name()));
    }

    @Test
    void inactivateTeam_devuelve200() throws Exception {
        mockMvc.perform(patch("/tournaments/t1/teams/team1/inactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(RegistrationStatus.INACTIVE.name()));
    }

    @Test
    void inactivateUser_devuelve200() throws Exception {
        mockMvc.perform(patch("/tournaments/t1/users/user1/inactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ParticipantStatus.INACTIVE.name()));
    }
}
