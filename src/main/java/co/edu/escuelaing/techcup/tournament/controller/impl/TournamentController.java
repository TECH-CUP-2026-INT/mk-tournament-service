package co.edu.escuelaing.techcup.tournament.controller.impl;

import co.edu.escuelaing.techcup.tournament.exception.InvalidCourtDataException;
import co.edu.escuelaing.techcup.tournament.service.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.CourtSection;
import co.edu.escuelaing.techcup.tournament.service.PreparationResult;
import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.ConsultHistoricalTournamentsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ConsultRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.GetEnrolledTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewRegisteredTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewMatchupsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewMatchCourtUseCase;
import co.edu.escuelaing.techcup.tournament.dto.response.EnrolledTeamResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.MatchCourtResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.RegisteredTeamResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.RegisteredTeamsResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.ReservedTeamResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.MatchupResponse;
import co.edu.escuelaing.techcup.tournament.service.ports.GetChampionUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.AssignChampionUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.AttachRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.AttachRulebookUseCase.AttachRulebookCommand;
import co.edu.escuelaing.techcup.tournament.service.ports.CheckTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.CreateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.DeleteTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.FinalizeTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.RegisterCourtUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.RegisterCourtUseCase.RegisterCourtCommand;
import co.edu.escuelaing.techcup.tournament.service.ports.EditTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.EditTournamentUseCase.EditTournamentCommand;
import co.edu.escuelaing.techcup.tournament.service.ports.PauseTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.PauseTournamentUseCase.PauseTournamentCommand;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateTournamentUseCase.InactivateTournamentCommand;
import co.edu.escuelaing.techcup.tournament.service.ports.DisqualifyTeamUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateTeamUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.InactivateUserUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.EnrollTeamInTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.Enrollment;
import co.edu.escuelaing.techcup.tournament.dto.request.CreateTournamentRequest;
import co.edu.escuelaing.techcup.tournament.dto.request.EditTournamentRequest;
import co.edu.escuelaing.techcup.tournament.dto.request.PauseTournamentRequest;
import co.edu.escuelaing.techcup.tournament.dto.request.InactivateTournamentRequest;
import co.edu.escuelaing.techcup.tournament.dto.request.DisqualifyTeamRequest;
import co.edu.escuelaing.techcup.tournament.dto.request.EnrollTeamRequest;
import co.edu.escuelaing.techcup.tournament.dto.response.ChampionResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.CourtResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.DeleteTournamentResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.PauseTournamentResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.InactivateTournamentResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.DisqualifyTeamResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.InactivateTeamResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.InactivateUserResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.EnrollmentResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.PreparationResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.RulebookResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.TournamentResponse;
import co.edu.escuelaing.techcup.tournament.mapper.TournamentRestMapper;
import co.edu.escuelaing.techcup.tournament.service.ports.StartTournamentPreparationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/tournaments")
@Tag(name = "Torneos", description = "Creación, edición y gestión del ciclo de vida de torneos")
public class TournamentController {

    private final CreateTournamentUseCase createTournamentUseCase;
    private final FinalizeTournamentUseCase finalizeTournamentUseCase;
    private final CheckTournamentPreparationUseCase checkPreparation;
    private final DeleteTournamentUseCase deleteTournamentUseCase;
    private final AssignChampionUseCase assignChampionUseCase;
    private final GetChampionUseCase getChampionUseCase;
    private final AttachRulebookUseCase attachRulebook;
    private final ConsultRulebookUseCase consultRulebook;
    private final RegisterCourtUseCase registerCourtUseCase;
    private final ConsultHistoricalTournamentsUseCase consultHistorical;
    private final GetEnrolledTeamsUseCase getEnrolledTeams;
    private final ViewRegisteredTeamsUseCase viewRegisteredTeams;
    private final EditTournamentUseCase editTournamentUseCase;
    private final PauseTournamentUseCase pauseTournamentUseCase;
    private final InactivateTournamentUseCase inactivateTournamentUseCase;
    private final DisqualifyTeamUseCase disqualifyTeamUseCase;
    private final InactivateTeamUseCase inactivateTeamUseCase;
    private final InactivateUserUseCase inactivateUserUseCase;
    private final EnrollTeamInTournamentUseCase enrollTeamInTournamentUseCase;
    private final StartTournamentPreparationUseCase startTournamentPreparation;
    private final ViewMatchupsUseCase viewMatchups;
    private final ViewMatchCourtUseCase viewMatchCourt;
    private final TournamentRestMapper mapper;

    public TournamentController(CreateTournamentUseCase createTournamentUseCase,
                                 FinalizeTournamentUseCase finalizeTournamentUseCase,
                                 CheckTournamentPreparationUseCase checkPreparation,
                                 DeleteTournamentUseCase deleteTournamentUseCase,
                                 AssignChampionUseCase assignChampionUseCase,
                                 GetChampionUseCase getChampionUseCase,
                                 AttachRulebookUseCase attachRulebook,
                                 ConsultRulebookUseCase consultRulebook,
                                 RegisterCourtUseCase registerCourtUseCase,
                                 ConsultHistoricalTournamentsUseCase consultHistorical,
                                 GetEnrolledTeamsUseCase getEnrolledTeams,
                                 ViewRegisteredTeamsUseCase viewRegisteredTeams,
                                 EditTournamentUseCase editTournamentUseCase,
                                 PauseTournamentUseCase pauseTournamentUseCase,
                                 InactivateTournamentUseCase inactivateTournamentUseCase,
                                 DisqualifyTeamUseCase disqualifyTeamUseCase,
                                 InactivateTeamUseCase inactivateTeamUseCase,
                                 InactivateUserUseCase inactivateUserUseCase,
                                 EnrollTeamInTournamentUseCase enrollTeamInTournamentUseCase,
                                 StartTournamentPreparationUseCase startTournamentPreparation,
                                 ViewMatchupsUseCase viewMatchups,
                                 ViewMatchCourtUseCase viewMatchCourt,
                                 TournamentRestMapper mapper) {
        this.createTournamentUseCase = createTournamentUseCase;
        this.finalizeTournamentUseCase = finalizeTournamentUseCase;
        this.checkPreparation = checkPreparation;
        this.deleteTournamentUseCase = deleteTournamentUseCase;
        this.assignChampionUseCase = assignChampionUseCase;
        this.getChampionUseCase = getChampionUseCase;
        this.attachRulebook = attachRulebook;
        this.consultRulebook = consultRulebook;
        this.registerCourtUseCase = registerCourtUseCase;
        this.consultHistorical = consultHistorical;
        this.getEnrolledTeams = getEnrolledTeams;
        this.viewRegisteredTeams = viewRegisteredTeams;
        this.editTournamentUseCase = editTournamentUseCase;
        this.pauseTournamentUseCase = pauseTournamentUseCase;
        this.inactivateTournamentUseCase = inactivateTournamentUseCase;
        this.disqualifyTeamUseCase = disqualifyTeamUseCase;
        this.inactivateTeamUseCase = inactivateTeamUseCase;
        this.inactivateUserUseCase = inactivateUserUseCase;
        this.enrollTeamInTournamentUseCase = enrollTeamInTournamentUseCase;
        this.startTournamentPreparation = startTournamentPreparation;
        this.viewMatchups = viewMatchups;
        this.viewMatchCourt = viewMatchCourt;
        this.mapper = mapper;
    }

    @Operation(summary = "Crear torneo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Torneo creado",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody CreateTournamentRequest request) {
        Tournament newTournament = Tournament.create(
                request.name(),
                request.type(),
                request.format(),
                request.numberOfTeams(),
                request.cost(),
                request.startDate(),
                request.endDate(),
                request.registrationDeadline(),
                request.matchStartTime(),
                request.matchEndTime()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(createTournamentUseCase.create(newTournament)));
    }

    @Operation(summary = "Finalizar torneo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneo finalizado",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "409", description = "El torneo no puede finalizarse en su estado actual", content = @Content)
    })
    @PatchMapping("/{id}/finalize")
    public ResponseEntity<TournamentResponse> finalize(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String id) {
        Tournament finalized = finalizeTournamentUseCase.finalizeTournament(id);

        return ResponseEntity.ok(mapper.toResponse(finalized));
    }

    @Operation(summary = "Iniciar fase de preparación")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preparación iniciada",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "409", description = "El torneo no cumple los requisitos para pasar a preparación", content = @Content)
    })
    @PatchMapping("/{id}/prepare")
    public ResponseEntity<TournamentResponse> prepare(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String id) {
        Tournament tournament = startTournamentPreparation.startPreparation(id);
        return ResponseEntity.ok(mapper.toResponse(tournament));
    }

    @Operation(summary = "Consultar estado de preparación")
    @ApiResponse(responseCode = "200", description = "Estado de preparación del torneo",
            content = @Content(schema = @Schema(implementation = PreparationResponse.class)))
    @GetMapping("/{tournamentId}/preparation")
    public ResponseEntity<PreparationResponse> checkPreparation(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId) {
        PreparationResult result = checkPreparation.check(tournamentId);
        String status = result.isReadyToActivate() ? "completo" : "incompleto";
        return ResponseEntity.ok(new PreparationResponse(status, result.isReadyToActivate(),
                result.getApprovedTeamsCount(), result.getMissingRequirements()));
    }

    @Operation(summary = "Asignar campeón del torneo",
            description = "Se dispara cuando el partido marcado como final finaliza. Si hay empate en tiempo reglamentario, requiere que ya se haya registrado el ganador de la tanda de penales.")
    @ApiResponse(responseCode = "200", description = "Campeón asignado",
            content = @Content(schema = @Schema(implementation = ChampionResponse.class)))
    @PostMapping("/{tournamentId}/matches/{matchId}/champion")
    public ResponseEntity<ChampionResponse> assignChampion(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId,
            @Parameter(description = "ID del partido final", example = "m01") @PathVariable String matchId) {
        ChampionAssignment assignment = assignChampionUseCase.assignChampion(tournamentId, matchId);
        return ResponseEntity.ok(new ChampionResponse(
                tournamentId, assignment.championTeamId(), assignment.resolution()));
    }

    @Operation(summary = "Consultar campeón del torneo")
    @ApiResponse(responseCode = "200", description = "Campeón del torneo",
            content = @Content(schema = @Schema(implementation = ChampionResponse.class)))
    @GetMapping("/{tournamentId}/champion")
    public ResponseEntity<ChampionResponse> getChampion(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId) {
        ChampionAssignment assignment = getChampionUseCase.getChampion(tournamentId);
        return ResponseEntity.ok(new ChampionResponse(
                tournamentId, assignment.championTeamId(), assignment.resolution()));
    }

    @Operation(summary = "Eliminar torneo (solo estado Borrador)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneo eliminado",
                    content = @Content(schema = @Schema(implementation = DeleteTournamentResponse.class))),
            @ApiResponse(responseCode = "409", description = "El torneo no está en estado Borrador", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteTournamentResponse> delete(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String id) {
        deleteTournamentUseCase.delete(id);
        return ResponseEntity.ok(new DeleteTournamentResponse(
                "El torneo '" + id + "' ha sido eliminado permanentemente."
        ));
    }

    @Operation(summary = "Consultar fixture / bracket")
    @ApiResponse(responseCode = "200", description = "Lista de emparejamientos del torneo",
            content = @Content(schema = @Schema(implementation = MatchupResponse.class)))
    @GetMapping("/{tournamentId}/matchups")
    public ResponseEntity<java.util.List<MatchupResponse>> getMatchups(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId) {
        java.util.List<MatchupResponse> result = viewMatchups.getMatchups(tournamentId)
                .stream()
                .map(m -> new MatchupResponse(
                        m.getMatchId(),
                        m.getHomeTeamId(),
                        m.getAwayTeamId(),
                        m.getStatus(),
                        m.getHomeScore(),
                        m.getAwayScore(),
                        m.isFinalMatch()
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Consultar cancha asignada a un partido")
    @ApiResponse(responseCode = "200", description = "Cancha asignada al partido (o estado pendiente si aún no se ha programado)",
            content = @Content(schema = @Schema(implementation = MatchCourtResponse.class)))
    @GetMapping("/matches/{matchId}/court")
    public ResponseEntity<MatchCourtResponse> getMatchCourt(
            @Parameter(description = "ID del partido", example = "m01") @PathVariable String matchId) {
        return viewMatchCourt.getCourtByMatch(matchId)
                .map(c -> ResponseEntity.ok(new MatchCourtResponse(
                        c.getId(), c.getMatchId(), c.getSection().name(),
                        c.getDescription(), c.getImageId(), null
                )))
                .orElse(ResponseEntity.ok(MatchCourtResponse.pending(matchId)));
    }

    @Operation(summary = "Listar equipos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de equipos registrados en el torneo",
            content = @Content(schema = @Schema(implementation = RegisteredTeamResponse.class)))
    @GetMapping("/{tournamentId}/teams")
    public ResponseEntity<java.util.List<RegisteredTeamResponse>> getRegisteredTeams(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId) {
        java.util.List<RegisteredTeamResponse> result = viewRegisteredTeams.getTeams(tournamentId)
                .stream()
                .map(t -> new RegisteredTeamResponse(
                        t.getTeamId(),
                        t.getTeamName(),
                        t.getRegistrationStatus(),
                        "https://placeholder.com/teams/" + t.getTeamId() + "/logo"
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Listar equipos inscritos y en reserva")
    @ApiResponse(responseCode = "200", description = "Equipos inscritos confirmados y equipos con cupo reservado pendiente de pago",
            content = @Content(schema = @Schema(implementation = RegisteredTeamsResponse.class)))
    @GetMapping("/{tournamentId}/enrollments")
    public ResponseEntity<RegisteredTeamsResponse> getEnrolledTeams(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId) {
        GetEnrolledTeamsUseCase.EnrolledTeamsView view = getEnrolledTeams.getEnrolledTeams(tournamentId);

        java.util.List<EnrolledTeamResponse> enrolledTeams = view.enrolled().stream()
                .map(e -> new EnrolledTeamResponse(
                        e.getTeamId(),
                        e.getTeamName(),
                        "https://placeholder.com/teams/" + e.getTeamId() + "/logo",
                        e.getEnrollmentId(),
                        e.getConfirmationDate()
                ))
                .toList();

        java.util.List<ReservedTeamResponse> reservedTeams = view.reserved().stream()
                .map(r -> new ReservedTeamResponse(
                        r.enrollment().getTeamId(),
                        r.enrollment().getTeamName(),
                        r.enrollment().getEnrollmentId(),
                        r.livePaymentStatus(),
                        r.enrollment().getReservationExpiresAt()
                ))
                .toList();

        return ResponseEntity.ok(new RegisteredTeamsResponse(
                enrolledTeams, reservedTeams, enrolledTeams.size(), reservedTeams.size(), view.availableSlots()));
    }

    @Operation(summary = "Inscribir equipo en torneo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Equipo inscrito",
                    content = @Content(schema = @Schema(implementation = EnrollmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "El equipo ya está inscrito o no hay cupo disponible", content = @Content)
    })
    @PostMapping("/{tournamentId}/enrollments")
    public ResponseEntity<EnrollmentResponse> enrollTeam(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId,
            @Valid @RequestBody EnrollTeamRequest request) {
        Enrollment enrollment = enrollTeamInTournamentUseCase.enrollTeam(tournamentId, request.teamId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new EnrollmentResponse(
                enrollment.getEnrollmentId(), enrollment.getStatus(), enrollment.getReservationExpiresAt()));
    }

    @Operation(summary = "Listar torneos históricos")
    @ApiResponse(responseCode = "200", description = "Lista de torneos finalizados",
            content = @Content(schema = @Schema(implementation = co.edu.escuelaing.techcup.tournament.dto.response.HistoricalTournamentResponse.class)))
    @GetMapping("/history")
    public ResponseEntity<java.util.List<co.edu.escuelaing.techcup.tournament.dto.response.HistoricalTournamentResponse>> getHistory() {
        java.util.List<co.edu.escuelaing.techcup.tournament.dto.response.HistoricalTournamentResponse> result =
                consultHistorical.findAll().stream()
                        .map(this::toHistoricalResponse)
                        .toList();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Consultar torneo histórico por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneo histórico encontrado",
                    content = @Content(schema = @Schema(implementation = co.edu.escuelaing.techcup.tournament.dto.response.HistoricalTournamentResponse.class))),
            @ApiResponse(responseCode = "404", description = "El torneo no existe o no está finalizado", content = @Content)
    })
    @GetMapping("/history/{tournamentId}")
    public ResponseEntity<co.edu.escuelaing.techcup.tournament.dto.response.HistoricalTournamentResponse> getHistoricalById(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId) {
        return ResponseEntity.ok(toHistoricalResponse(consultHistorical.findById(tournamentId)));
    }

    private co.edu.escuelaing.techcup.tournament.dto.response.HistoricalTournamentResponse toHistoricalResponse(
            co.edu.escuelaing.techcup.tournament.service.Tournament t) {
        return new co.edu.escuelaing.techcup.tournament.dto.response.HistoricalTournamentResponse(
                t.getId(), t.getName(), t.getNumberOfTeams(), t.getCost(),
                t.getStartDate(), t.getEndDate(), t.getRegistrationDeadline(),
                t.getStatus(), t.getChampionTeamId()
        );
    }

    @Operation(summary = "Descargar reglamento (PDF)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Archivo PDF del reglamento",
                    content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE)),
            @ApiResponse(responseCode = "404", description = "El torneo no tiene reglamento adjunto", content = @Content)
    })
    @GetMapping("/{tournamentId}/rulebook")
    public ResponseEntity<org.springframework.core.io.InputStreamResource> consultRulebook(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId) {
        ConsultRulebookUseCase.RulebookResource resource = consultRulebook.consult(tournamentId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + resource.fileName() + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(new org.springframework.core.io.InputStreamResource(resource.content()));
    }

    @Operation(summary = "Adjuntar reglamento (PDF)", description = "El cuerpo de la petición es multipart/form-data.")
    @ApiResponse(responseCode = "200", description = "Reglamento adjuntado",
            content = @Content(schema = @Schema(implementation = RulebookResponse.class)))
    @PostMapping(value = "/{tournamentId}/rulebook", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RulebookResponse> attachRulebook(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId,
            @Parameter(description = "Archivo PDF del reglamento (máx. 10 MB)") @RequestParam("file") MultipartFile file) throws IOException {

        Tournament updated = attachRulebook.attach(new AttachRulebookCommand(
                tournamentId,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getInputStream()
        ));

        return ResponseEntity.ok(new RulebookResponse(
                updated.getId(), updated.getRulebookFileId(), "Reglamento adjuntado correctamente"
        ));
    }

    @Operation(summary = "Registrar cancha", description = "El cuerpo de la petición es multipart/form-data.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cancha registrada",
                    content = @Content(schema = @Schema(implementation = CourtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Sección de cancha inválida o imagen inválida", content = @Content)
    })
    @PostMapping(value = "/{tournamentId}/courts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourtResponse> registerCourt(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId,
            @Parameter(description = "Sección de la cancha en el mapa del campus") @RequestParam("section") String section,
            @Parameter(description = "Descripción de la cancha") @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "Imagen de la cancha") @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        CourtSection courtSection;
        try {
            courtSection = CourtSection.valueOf(section);
        } catch (IllegalArgumentException e) {
            throw new InvalidCourtDataException("Sección de cancha inválida: " + section);
        }

        Court court = registerCourtUseCase.register(new RegisterCourtCommand(
                tournamentId,
                courtSection,
                description,
                image != null ? image.getOriginalFilename() : null,
                image != null ? image.getContentType() : null,
                image != null ? image.getSize() : null,
                image != null ? image.getInputStream() : null
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(new CourtResponse(
                court.getId(), court.getTournamentId(), court.getSection().name(),
                court.getDescription(), court.getImageId(), "Cancha registrada correctamente"
        ));
    }

    @Operation(summary = "Editar torneo", description = "Permite modificar cualquier campo definido en la creación del torneo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneo editado",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "El torneo no existe", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<TournamentResponse> edit(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String id,
            @Valid @RequestBody EditTournamentRequest request) {
        Tournament updated = editTournamentUseCase.edit(new EditTournamentCommand(
                id,
                request.name(),
                request.type(),
                request.format(),
                request.numberOfTeams(),
                request.cost(),
                request.registrationDeadline(),
                request.startDate(),
                request.endDate(),
                request.matchStartTime(),
                request.matchEndTime()
        ));

        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @Operation(summary = "Pausar o reanudar torneo",
            description = "Pausar suspende el registro de eventos, pero todos los datos siguen siendo consultables.")
    @ApiResponse(responseCode = "200", description = "Torneo pausado o reanudado",
            content = @Content(schema = @Schema(implementation = PauseTournamentResponse.class)))
    @PatchMapping("/{id}/pause")
    public ResponseEntity<PauseTournamentResponse> pause(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String id,
            @Valid @RequestBody PauseTournamentRequest request) {
        Tournament updated = pauseTournamentUseCase.execute(new PauseTournamentCommand(id, request.action()));

        String message = updated.isPaused()
                ? "El torneo fue pausado correctamente"
                : "El torneo fue reanudado correctamente";

        return ResponseEntity.ok(new PauseTournamentResponse(
                updated.getId(), updated.getStatus(), updated.isPaused(), message));
    }

    @Operation(summary = "Inactivar o reactivar torneo",
            description = "Inactivar bloquea TODAS las funcionalidades del torneo, incluidas las consultas.")
    @ApiResponse(responseCode = "200", description = "Torneo inactivado o reactivado",
            content = @Content(schema = @Schema(implementation = InactivateTournamentResponse.class)))
    @PatchMapping("/{id}/inactivate")
    public ResponseEntity<InactivateTournamentResponse> inactivate(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String id,
            @Valid @RequestBody InactivateTournamentRequest request) {
        Tournament updated = inactivateTournamentUseCase.execute(new InactivateTournamentCommand(id, request.action()));

        String message = updated.isActive()
                ? "El torneo fue reactivado correctamente"
                : "El torneo fue inactivado correctamente";

        return ResponseEntity.ok(new InactivateTournamentResponse(
                updated.getId(), updated.getStatus(), updated.isActive(), message));
    }

    @Operation(summary = "Descalificar equipo")
    @ApiResponse(responseCode = "200", description = "Equipo descalificado",
            content = @Content(schema = @Schema(implementation = DisqualifyTeamResponse.class)))
    @PatchMapping("/{tournamentId}/teams/{teamId}/disqualify")
    public ResponseEntity<DisqualifyTeamResponse> disqualifyTeam(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId,
            @Parameter(description = "ID del equipo", example = "team_xyz789") @PathVariable String teamId,
            @Valid @RequestBody DisqualifyTeamRequest request) {
        disqualifyTeamUseCase.disqualify(tournamentId, teamId, request.reason());

        return ResponseEntity.ok(new DisqualifyTeamResponse(
                tournamentId, teamId, RegistrationStatus.DISQUALIFIED,
                "El equipo fue descalificado correctamente"));
    }

    @Operation(summary = "Inactivar equipo en torneo",
            description = "El equipo inactivado no recibe programación ni puntos; es una medida administrativa temporal.")
    @ApiResponse(responseCode = "200", description = "Equipo inactivado",
            content = @Content(schema = @Schema(implementation = InactivateTeamResponse.class)))
    @PatchMapping("/{tournamentId}/teams/{teamId}/inactivate")
    public ResponseEntity<InactivateTeamResponse> inactivateTeam(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId,
            @Parameter(description = "ID del equipo", example = "team_xyz789") @PathVariable String teamId) {
        inactivateTeamUseCase.inactivate(tournamentId, teamId);

        return ResponseEntity.ok(new InactivateTeamResponse(
                tournamentId, teamId, RegistrationStatus.INACTIVE,
                "El equipo fue inactivado correctamente"));
    }

    @Operation(summary = "Inactivar usuario participante",
            description = "El usuario inactivado no puede entrar en alineaciones ni acumular estadísticas en ese torneo.")
    @ApiResponse(responseCode = "200", description = "Usuario inactivado",
            content = @Content(schema = @Schema(implementation = InactivateUserResponse.class)))
    @PatchMapping("/{tournamentId}/users/{userId}/inactivate")
    public ResponseEntity<InactivateUserResponse> inactivateUser(
            @Parameter(description = "ID del torneo", example = "abc123") @PathVariable String tournamentId,
            @Parameter(description = "ID del usuario", example = "user_123") @PathVariable String userId) {
        inactivateUserUseCase.inactivate(tournamentId, userId);

        return ResponseEntity.ok(new InactivateUserResponse(
                tournamentId, userId,
                co.edu.escuelaing.techcup.tournament.service.ParticipantStatus.INACTIVE,
                "El usuario fue inactivado correctamente en el torneo"));
    }
}
