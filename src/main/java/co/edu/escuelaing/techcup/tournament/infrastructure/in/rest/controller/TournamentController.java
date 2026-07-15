package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidCourtDataException;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import co.edu.escuelaing.techcup.tournament.domain.model.PreparationResult;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ConsultHistoricalTournamentsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ConsultRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetEnrolledTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewRegisteredTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewMatchupsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewMatchCourtUseCase;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.EnrolledTeamResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.MatchCourtResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.RegisteredTeamResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.RegisteredTeamsResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.ReservedTeamResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.MatchupResponse;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetChampionUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.AssignChampionUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.AttachRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.AttachRulebookUseCase.AttachRulebookCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.CheckTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.CreateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.DeleteTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.FinalizeTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RegisterCourtUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RegisterCourtUseCase.RegisterCourtCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.EditTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.EditTournamentUseCase.EditTournamentCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.PauseTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.PauseTournamentUseCase.PauseTournamentCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateTournamentUseCase.InactivateTournamentCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.DisqualifyTeamUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateTeamUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateUserUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.EnrollTeamInTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.CreateTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.EditTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.PauseTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.InactivateTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.DisqualifyTeamRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.EnrollTeamRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.ChampionResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.CourtResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.DeleteTournamentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.PauseTournamentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.InactivateTournamentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.DisqualifyTeamResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.InactivateTeamResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.InactivateUserResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.EnrollmentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.PreparationResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.RulebookResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.TournamentResponse;
import co.edu.escuelaing.techcup.tournament.application.mapper.EnrollmentRestMapper;
import co.edu.escuelaing.techcup.tournament.application.mapper.MatchupRestMapper;
import co.edu.escuelaing.techcup.tournament.application.mapper.TournamentRestMapper;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.StartTournamentPreparationUseCase;
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
@Tag(name = "Tournaments", description = "Creating, editing, and managing the full tournament lifecycle")
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
    private final MatchupRestMapper matchupRestMapper;
    private final EnrollmentRestMapper enrollmentRestMapper;

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
                                 TournamentRestMapper mapper,
                                 MatchupRestMapper matchupRestMapper,
                                 EnrollmentRestMapper enrollmentRestMapper) {
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
        this.matchupRestMapper = matchupRestMapper;
        this.enrollmentRestMapper = enrollmentRestMapper;
    }

    @Operation(summary = "Create tournament",
            description = "Creates a tournament directly in ACTIVE status. See CreateTournamentRequest for the "
                    + "NORMAL vs LIGHTNING field rules.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tournament created",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody CreateTournamentRequest request) {
        Tournament created = createTournamentUseCase.create(new CreateTournamentUseCase.CreateTournamentCommand(
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
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @Operation(summary = "Finalize tournament",
            description = "Moves the tournament to FINISHED and archives it to the historical read-only view. "
                    + "Only allowed once the tournament is In Progress and the end date has been reached.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tournament finalized",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "409", description = "The tournament cannot be finalized in its current state", content = @Content)
    })
    @PatchMapping("/{id}/finalize")
    public ResponseEntity<TournamentResponse> finalize(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String id) {
        Tournament finalized = finalizeTournamentUseCase.finalizeTournament(id);

        return ResponseEntity.ok(mapper.toResponse(finalized));
    }

    @Operation(summary = "Start preparation phase",
            description = "Moves the tournament to IN_PREPARATION and generates its fixture (matchups) at random, "
                    + "based on the tournament's format. Requires at least 3 approved teams.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preparation started, fixture generated",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "409", description = "The tournament does not meet the requirements to enter preparation", content = @Content)
    })
    @PatchMapping("/{id}/prepare")
    public ResponseEntity<TournamentResponse> prepare(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String id) {
        Tournament tournament = startTournamentPreparation.startPreparation(id);
        return ResponseEntity.ok(mapper.toResponse(tournament));
    }

    @Operation(summary = "Check preparation readiness",
            description = "Reports whether the tournament already meets the requirements to move into preparation "
                    + "(at least 3 approved teams), and lists what's missing otherwise.")
    @ApiResponse(responseCode = "200", description = "Tournament preparation readiness",
            content = @Content(schema = @Schema(implementation = PreparationResponse.class)))
    @GetMapping("/{tournamentId}/preparation")
    public ResponseEntity<PreparationResponse> checkPreparation(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId) {
        PreparationResult result = checkPreparation.check(tournamentId);
        String status = result.isReadyToActivate() ? "complete" : "incomplete";
        return ResponseEntity.ok(new PreparationResponse(status, result.isReadyToActivate(),
                result.getApprovedTeamsCount(), result.getMissingRequirements()));
    }

    @Operation(summary = "Assign tournament champion",
            description = "Triggered when the match marked as the final finishes. If the score is tied in "
                    + "regulation time, the penalty shootout winner must already have been recorded first.")
    @ApiResponse(responseCode = "200", description = "Champion assigned",
            content = @Content(schema = @Schema(implementation = ChampionResponse.class)))
    @PostMapping("/{tournamentId}/matches/{matchId}/champion")
    public ResponseEntity<ChampionResponse> assignChampion(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId,
            @Parameter(description = "ID of the final match", example = "m01") @PathVariable String matchId) {
        ChampionAssignment assignment = assignChampionUseCase.assignChampion(tournamentId, matchId);
        return ResponseEntity.ok(new ChampionResponse(
                tournamentId, assignment.championTeamId(), assignment.resolution()));
    }

    @Operation(summary = "Get tournament champion")
    @ApiResponse(responseCode = "200", description = "Tournament champion",
            content = @Content(schema = @Schema(implementation = ChampionResponse.class)))
    @GetMapping("/{tournamentId}/champion")
    public ResponseEntity<ChampionResponse> getChampion(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId) {
        ChampionAssignment assignment = getChampionUseCase.getChampion(tournamentId);
        return ResponseEntity.ok(new ChampionResponse(
                tournamentId, assignment.championTeamId(), assignment.resolution()));
    }

    @Operation(summary = "Delete tournament (Draft status only)",
            description = "Permanently deletes a tournament. Only allowed while it is still in Draft status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tournament deleted",
                    content = @Content(schema = @Schema(implementation = DeleteTournamentResponse.class))),
            @ApiResponse(responseCode = "409", description = "The tournament is not in Draft status", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteTournamentResponse> delete(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String id) {
        deleteTournamentUseCase.delete(id);
        return ResponseEntity.ok(new DeleteTournamentResponse(
                "Tournament '" + id + "' has been permanently deleted."
        ));
    }

    @Operation(summary = "Get fixture / bracket",
            description = "Returns every matchup generated for the tournament, with current results. Slots not yet "
                    + "resolved (future bracket rounds) are returned with null team IDs.")
    @ApiResponse(responseCode = "200", description = "List of tournament matchups",
            content = @Content(schema = @Schema(implementation = MatchupResponse.class)))
    @GetMapping("/{tournamentId}/matchups")
    public ResponseEntity<java.util.List<MatchupResponse>> getMatchups(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId) {
        java.util.List<MatchupResponse> result = viewMatchups.getMatchups(tournamentId)
                .stream()
                .map(matchupRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get the court assigned to a match",
            description = "Returns a pending placeholder (200, with a message) instead of 404 if the match hasn't "
                    + "been scheduled to a court yet.")
    @ApiResponse(responseCode = "200", description = "Court assigned to the match (or a pending state if not scheduled yet)",
            content = @Content(schema = @Schema(implementation = MatchCourtResponse.class)))
    @GetMapping("/matches/{matchId}/court")
    public ResponseEntity<MatchCourtResponse> getMatchCourt(
            @Parameter(description = "Match ID", example = "m01") @PathVariable String matchId) {
        return viewMatchCourt.getCourtByMatch(matchId)
                .map(c -> ResponseEntity.ok(new MatchCourtResponse(
                        c.getId(), c.getMatchId(), c.getSection().name(),
                        c.getDescription(), c.getImageId(), null
                )))
                .orElse(ResponseEntity.ok(MatchCourtResponse.pending(matchId)));
    }

    @Operation(summary = "List registered teams",
            description = "Legacy registration view (name + status). For payment/reservation details, use "
                    + "GET /tournaments/{tournamentId}/enrollments instead.")
    @ApiResponse(responseCode = "200", description = "List of teams registered in the tournament",
            content = @Content(schema = @Schema(implementation = RegisteredTeamResponse.class)))
    @GetMapping("/{tournamentId}/teams")
    public ResponseEntity<java.util.List<RegisteredTeamResponse>> getRegisteredTeams(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId) {
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

    @Operation(summary = "List enrolled and reserved teams",
            description = "Returns teams with a confirmed (paid) enrollment separately from teams with a slot "
                    + "reservation still awaiting payment confirmation, plus how many slots remain open.")
    @ApiResponse(responseCode = "200", description = "Enrolled teams and reserved teams",
            content = @Content(schema = @Schema(implementation = RegisteredTeamsResponse.class)))
    @GetMapping("/{tournamentId}/enrollments")
    public ResponseEntity<RegisteredTeamsResponse> getEnrolledTeams(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId) {
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

    @Operation(summary = "Enroll team in tournament",
            description = "The team captain enrolls their team. The tournament must be ACTIVE and have open slots; "
                    + "the enrollment starts as RESERVED/PENDING_PAYMENT until payment-service confirms payment.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Team enrolled",
                    content = @Content(schema = @Schema(implementation = EnrollmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "409", description = "The team is already enrolled, or there are no open slots", content = @Content)
    })
    @PostMapping("/{tournamentId}/enrollments")
    public ResponseEntity<EnrollmentResponse> enrollTeam(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId,
            @Valid @RequestBody EnrollTeamRequest request) {
        Enrollment enrollment = enrollTeamInTournamentUseCase.enrollTeam(tournamentId, request.teamId());
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentRestMapper.toResponse(enrollment));
    }

    @Operation(summary = "List historical tournaments",
            description = "Read-only list of every finished tournament, accessible without authentication.")
    @ApiResponse(responseCode = "200", description = "List of finished tournaments",
            content = @Content(schema = @Schema(implementation = co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.HistoricalTournamentResponse.class)))
    @GetMapping("/history")
    public ResponseEntity<java.util.List<co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.HistoricalTournamentResponse>> getHistory() {
        java.util.List<co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.HistoricalTournamentResponse> result =
                consultHistorical.findAll().stream()
                        .map(mapper::toHistoricalResponse)
                        .toList();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get historical tournament by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historical tournament found",
                    content = @Content(schema = @Schema(implementation = co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.HistoricalTournamentResponse.class))),
            @ApiResponse(responseCode = "404", description = "The tournament doesn't exist or hasn't finished yet", content = @Content)
    })
    @GetMapping("/history/{tournamentId}")
    public ResponseEntity<co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.HistoricalTournamentResponse> getHistoricalById(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId) {
        return ResponseEntity.ok(mapper.toHistoricalResponse(consultHistorical.findById(tournamentId)));
    }

    @Operation(summary = "Download rulebook (PDF)",
            description = "Streams the tournament's rulebook file inline as application/pdf.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rulebook PDF file",
                    content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE)),
            @ApiResponse(responseCode = "404", description = "The tournament has no rulebook attached", content = @Content)
    })
    @GetMapping("/{tournamentId}/rulebook")
    public ResponseEntity<org.springframework.core.io.InputStreamResource> consultRulebook(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId) {
        ConsultRulebookUseCase.RulebookResource resource = consultRulebook.consult(tournamentId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + resource.fileName() + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(new org.springframework.core.io.InputStreamResource(resource.content()));
    }

    @Operation(summary = "Attach rulebook (PDF)",
            description = "Uploads and attaches the official rulebook PDF (max. 10 MB) to the tournament. "
                    + "The request body is multipart/form-data.")
    @ApiResponse(responseCode = "200", description = "Rulebook attached",
            content = @Content(schema = @Schema(implementation = RulebookResponse.class)))
    @PostMapping(value = "/{tournamentId}/rulebook", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RulebookResponse> attachRulebook(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId,
            @Parameter(description = "Rulebook PDF file (max. 10 MB)") @RequestParam("file") MultipartFile file) throws IOException {

        Tournament updated = attachRulebook.attach(new AttachRulebookCommand(
                tournamentId,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getInputStream()
        ));

        return ResponseEntity.ok(new RulebookResponse(
                updated.getId(), updated.getRulebookFileId(), "Rulebook attached successfully"
        ));
    }

    @Operation(summary = "Register court",
            description = "Registers a court on one of the 4 campus map sections, with an optional description and "
                    + "image. The request body is multipart/form-data.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Court registered",
                    content = @Content(schema = @Schema(implementation = CourtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid court section or invalid image", content = @Content)
    })
    @PostMapping(value = "/{tournamentId}/courts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourtResponse> registerCourt(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId,
            @Parameter(description = "Campus map section for the court: CANCHA_1, CANCHA_2, CANCHA_3 or CANCHA_4",
                    example = "CANCHA_1") @RequestParam("section") String section,
            @Parameter(description = "Court description", example = "Synthetic grass court, north side of campus")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "Court image file") @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        CourtSection courtSection;
        try {
            courtSection = CourtSection.valueOf(section);
        } catch (IllegalArgumentException e) {
            throw new InvalidCourtDataException("Invalid court section: " + section);
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
                court.getDescription(), court.getImageId(), "Court registered successfully"
        ));
    }

    @Operation(summary = "Edit tournament",
            description = "Updates any field defined at tournament creation. Every field in the request body is "
                    + "optional — omitted (null) fields keep their current value. Blocked once the tournament is FINISHED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tournament edited",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "The tournament doesn't exist", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<TournamentResponse> edit(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String id,
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

    @Operation(summary = "Pause or resume tournament",
            description = "Pausing suspends new event registration but keeps all existing data queryable; "
                    + "resuming lifts that suspension.")
    @ApiResponse(responseCode = "200", description = "Tournament paused or resumed",
            content = @Content(schema = @Schema(implementation = PauseTournamentResponse.class)))
    @PatchMapping("/{id}/pause")
    public ResponseEntity<PauseTournamentResponse> pause(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String id,
            @Valid @RequestBody PauseTournamentRequest request) {
        Tournament updated = pauseTournamentUseCase.execute(new PauseTournamentCommand(id, request.action()));

        String message = updated.isPaused()
                ? "The tournament was successfully paused"
                : "The tournament was successfully resumed";

        return ResponseEntity.ok(new PauseTournamentResponse(
                updated.getId(), updated.getStatus(), updated.isPaused(), message));
    }

    @Operation(summary = "Inactivate or reactivate tournament",
            description = "Inactivating blocks ALL functionality of the tournament, including read queries — "
                    + "not just writes (unlike pause).")
    @ApiResponse(responseCode = "200", description = "Tournament inactivated or reactivated",
            content = @Content(schema = @Schema(implementation = InactivateTournamentResponse.class)))
    @PatchMapping("/{id}/inactivate")
    public ResponseEntity<InactivateTournamentResponse> inactivate(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String id,
            @Valid @RequestBody InactivateTournamentRequest request) {
        Tournament updated = inactivateTournamentUseCase.execute(new InactivateTournamentCommand(id, request.action()));

        String message = updated.isActive()
                ? "The tournament was successfully reactivated"
                : "The tournament was successfully inactivated";

        return ResponseEntity.ok(new InactivateTournamentResponse(
                updated.getId(), updated.getStatus(), updated.isActive(), message));
    }

    @Operation(summary = "Disqualify team",
            description = "Marks the team as disqualified. It stays in the records with its past results, but is "
                    + "excluded from any future matchups.")
    @ApiResponse(responseCode = "200", description = "Team disqualified",
            content = @Content(schema = @Schema(implementation = DisqualifyTeamResponse.class)))
    @PatchMapping("/{tournamentId}/teams/{teamId}/disqualify")
    public ResponseEntity<DisqualifyTeamResponse> disqualifyTeam(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId,
            @Parameter(description = "Team ID", example = "team_xyz789") @PathVariable String teamId,
            @Valid @RequestBody DisqualifyTeamRequest request) {
        disqualifyTeamUseCase.disqualify(tournamentId, teamId, request.reason());

        return ResponseEntity.ok(new DisqualifyTeamResponse(
                tournamentId, teamId, RegistrationStatus.DISQUALIFIED,
                "The team was successfully disqualified"));
    }

    @Operation(summary = "Inactivate team in tournament",
            description = "The inactivated team receives no scheduling or points; this is a temporary "
                    + "administrative measure, distinct from disqualification.")
    @ApiResponse(responseCode = "200", description = "Team inactivated",
            content = @Content(schema = @Schema(implementation = InactivateTeamResponse.class)))
    @PatchMapping("/{tournamentId}/teams/{teamId}/inactivate")
    public ResponseEntity<InactivateTeamResponse> inactivateTeam(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId,
            @Parameter(description = "Team ID", example = "team_xyz789") @PathVariable String teamId) {
        inactivateTeamUseCase.inactivate(tournamentId, teamId);

        return ResponseEntity.ok(new InactivateTeamResponse(
                tournamentId, teamId, RegistrationStatus.INACTIVE,
                "The team was successfully inactivated"));
    }

    @Operation(summary = "Inactivate participant user",
            description = "The inactivated user cannot be included in lineups or accumulate statistics in that "
                    + "tournament.")
    @ApiResponse(responseCode = "200", description = "User inactivated",
            content = @Content(schema = @Schema(implementation = InactivateUserResponse.class)))
    @PatchMapping("/{tournamentId}/users/{userId}/inactivate")
    public ResponseEntity<InactivateUserResponse> inactivateUser(
            @Parameter(description = "Tournament ID", example = "abc123") @PathVariable String tournamentId,
            @Parameter(description = "User ID", example = "user_123") @PathVariable String userId) {
        inactivateUserUseCase.inactivate(tournamentId, userId);

        return ResponseEntity.ok(new InactivateUserResponse(
                tournamentId, userId,
                co.edu.escuelaing.techcup.tournament.domain.model.ParticipantStatus.INACTIVE,
                "The user was successfully inactivated in the tournament"));
    }
}
