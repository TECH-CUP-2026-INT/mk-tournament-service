package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidCourtDataException;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.PreparationResult;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ConsultHistoricalTournamentsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ConsultRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetEnrolledTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewRegisteredTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewMatchupsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewMatchCourtUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewCourtMapUseCase;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller.swagger.TournamentControllerSwagger;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.EnrolledTeamResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.HistoricalTournamentResponse;
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
import co.edu.escuelaing.techcup.tournament.domain.model.ParticipantStatus;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.CreateTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.EditTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.PauseTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.InactivateTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.DisqualifyTeamRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.EnrollTeamRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.ChampionResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.CourtMapEntryResponse;
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
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RecordPenaltyShootoutWinnerUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.StartTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.RecordPenaltyShootoutWinnerRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class TournamentController implements TournamentControllerSwagger {

    private final CreateTournamentUseCase createTournamentUseCase;
    private final FinalizeTournamentUseCase finalizeTournamentUseCase;
    private final CheckTournamentPreparationUseCase checkPreparation;
    private final DeleteTournamentUseCase deleteTournamentUseCase;
    private final AssignChampionUseCase assignChampionUseCase;
    private final RecordPenaltyShootoutWinnerUseCase recordPenaltyShootoutWinnerUseCase;
    private final GetChampionUseCase getChampionUseCase;
    private final AttachRulebookUseCase attachRulebook;
    private final ConsultRulebookUseCase consultRulebook;
    private final RegisterCourtUseCase registerCourtUseCase;
    private final ViewCourtMapUseCase viewCourtMapUseCase;
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

    @Override
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

    @Override
    @PatchMapping("/{id}/finalize")
    public ResponseEntity<TournamentResponse> finalizeTournament(@PathVariable String id) {
        Tournament finalized = finalizeTournamentUseCase.finalizeTournament(id);

        return ResponseEntity.ok(mapper.toResponse(finalized));
    }

    @Override
    @PatchMapping("/{id}/prepare")
    public ResponseEntity<TournamentResponse> prepare(@PathVariable String id) {
        Tournament tournament = startTournamentPreparation.startPreparation(id);
        return ResponseEntity.ok(mapper.toResponse(tournament));
    }

    @Override
    @GetMapping("/{tournamentId}/preparation")
    public ResponseEntity<PreparationResponse> checkPreparation(@PathVariable String tournamentId) {
        PreparationResult result = checkPreparation.check(tournamentId);
        String status = result.isReadyToActivate() ? "complete" : "incomplete";
        return ResponseEntity.ok(new PreparationResponse(status, result.isReadyToActivate(),
                result.getApprovedTeamsCount(), result.getMissingRequirements()));
    }

    @Override
    @PostMapping("/{tournamentId}/matches/{matchId}/champion")
    public ResponseEntity<ChampionResponse> assignChampion(
            @PathVariable String tournamentId, @PathVariable String matchId) {
        ChampionAssignment assignment = assignChampionUseCase.assignChampion(tournamentId, matchId);
        return ResponseEntity.ok(new ChampionResponse(
                tournamentId, assignment.championTeamId(), assignment.resolution()));
    }

    @Override
    @PostMapping("/{tournamentId}/matches/{matchId}/penalty-shootout")
    public ResponseEntity<Void> recordPenaltyShootoutWinner(
            @PathVariable String tournamentId, @PathVariable String matchId,
            @Valid @RequestBody RecordPenaltyShootoutWinnerRequest request) {
        recordPenaltyShootoutWinnerUseCase.recordWinner(new RecordPenaltyShootoutWinnerUseCase.RecordPenaltyShootoutWinnerCommand(
                tournamentId, matchId, request.winnerTeamId()));
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/{tournamentId}/champion")
    public ResponseEntity<ChampionResponse> getChampion(@PathVariable String tournamentId) {
        ChampionAssignment assignment = getChampionUseCase.getChampion(tournamentId);
        return ResponseEntity.ok(new ChampionResponse(
                tournamentId, assignment.championTeamId(), assignment.resolution()));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteTournamentResponse> delete(@PathVariable String id) {
        deleteTournamentUseCase.delete(id);
        return ResponseEntity.ok(new DeleteTournamentResponse(
                "Tournament '" + id + "' has been permanently deleted."
        ));
    }

    @Override
    @GetMapping("/{tournamentId}/matchups")
    public ResponseEntity<List<MatchupResponse>> getMatchups(@PathVariable String tournamentId) {
        List<MatchupResponse> result = viewMatchups.getMatchups(tournamentId)
                .stream()
                .map(matchupRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    @Override
    @GetMapping("/matches/{matchId}/court")
    public ResponseEntity<MatchCourtResponse> getMatchCourt(@PathVariable String matchId) {
        return viewMatchCourt.getCourtByMatch(matchId)
                .map(c -> ResponseEntity.ok(new MatchCourtResponse(
                        c.getId(), c.getMatchId(), c.getSection().name(),
                        c.getDescription(), c.getImageId(), null
                )))
                .orElse(ResponseEntity.ok(MatchCourtResponse.pending(matchId)));
    }

    @Override
    @GetMapping("/{tournamentId}/teams")
    public ResponseEntity<List<RegisteredTeamResponse>> getRegisteredTeams(@PathVariable String tournamentId) {
        List<RegisteredTeamResponse> result = viewRegisteredTeams.getTeams(tournamentId)
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

    @Override
    @GetMapping("/{tournamentId}/enrollments")
    public ResponseEntity<RegisteredTeamsResponse> getEnrolledTeams(@PathVariable String tournamentId) {
        GetEnrolledTeamsUseCase.EnrolledTeamsView view = getEnrolledTeams.getEnrolledTeams(tournamentId);

        List<EnrolledTeamResponse> enrolledTeams = view.enrolled().stream()
                .map(e -> new EnrolledTeamResponse(
                        e.getTeamId(),
                        e.getTeamName(),
                        "https://placeholder.com/teams/" + e.getTeamId() + "/logo",
                        e.getEnrollmentId(),
                        e.getConfirmationDate()
                ))
                .toList();

        List<ReservedTeamResponse> reservedTeams = view.reserved().stream()
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

    @Override
    @PostMapping("/{tournamentId}/enrollments")
    public ResponseEntity<EnrollmentResponse> enrollTeam(
            @PathVariable String tournamentId,
            @Valid @RequestBody EnrollTeamRequest request) {
        Enrollment enrollment = enrollTeamInTournamentUseCase.enrollTeam(tournamentId, request.teamId());
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentRestMapper.toResponse(enrollment));
    }

    @Override
    @GetMapping("/history")
    public ResponseEntity<List<HistoricalTournamentResponse>> getHistory() {
        List<HistoricalTournamentResponse> result =
                consultHistorical.findAll().stream()
                        .map(mapper::toHistoricalResponse)
                        .toList();
        return ResponseEntity.ok(result);
    }

    @Override
    @GetMapping("/history/{tournamentId}")
    public ResponseEntity<HistoricalTournamentResponse> getHistoricalById(@PathVariable String tournamentId) {
        return ResponseEntity.ok(mapper.toHistoricalResponse(consultHistorical.findById(tournamentId)));
    }

    @Override
    @GetMapping("/{tournamentId}/rulebook")
    public ResponseEntity<InputStreamResource> consultRulebook(@PathVariable String tournamentId) {
        ConsultRulebookUseCase.RulebookResource resource = consultRulebook.consult(tournamentId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + resource.fileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(resource.content()));
    }

    @Override
    @PostMapping(value = "/{tournamentId}/rulebook", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RulebookResponse> attachRulebook(
            @PathVariable String tournamentId,
            @RequestParam("file") MultipartFile file) throws IOException {

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

    @Override
    @PostMapping(value = "/{tournamentId}/courts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourtResponse> registerCourt(
            @PathVariable String tournamentId,
            @RequestParam("section") String section,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

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

    @Override
    @GetMapping("/{tournamentId}/courts")
    public ResponseEntity<List<CourtMapEntryResponse>> getCourtMap(@PathVariable String tournamentId) {
        List<CourtMapEntryResponse> result = viewCourtMapUseCase.getCourtMap(tournamentId).stream()
                .map(this::toCourtMapEntryResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    private CourtMapEntryResponse toCourtMapEntryResponse(ViewCourtMapUseCase.CourtMapEntry entry) {
        Court court = entry.court();
        Match match = entry.match();
        ScheduledMatch scheduledMatch = entry.scheduledMatch();

        CourtMapEntryResponse.CourtMapStatus status;
        String statusLabel;
        if (match == null) {
            status = CourtMapEntryResponse.CourtMapStatus.AVAILABLE;
            statusLabel = "Available";
        } else {
            switch (match.getStatus()) {
                case IN_PROGRESS -> {
                    status = CourtMapEntryResponse.CourtMapStatus.IN_PROGRESS;
                    statusLabel = "In Progress";
                }
                case FINISHED, FINISHED_NO_SHOW -> {
                    status = CourtMapEntryResponse.CourtMapStatus.FINISHED;
                    statusLabel = "Finished";
                }
                default -> {
                    status = CourtMapEntryResponse.CourtMapStatus.SCHEDULED;
                    statusLabel = "Scheduled";
                }
            }
        }

        return new CourtMapEntryResponse(
                court.getId(),
                court.getSection(),
                court.getDescription(),
                court.getImageId(),
                status,
                statusLabel,
                match != null ? match.getMatchId() : null,
                match != null ? match.getHomeTeamId() : null,
                match != null ? match.getAwayTeamId() : null,
                scheduledMatch != null ? scheduledMatch.getMatchDate() : null,
                scheduledMatch != null ? scheduledMatch.getMatchTime() : null
        );
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<TournamentResponse> edit(
            @PathVariable String id,
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

    @Override
    @PatchMapping("/{id}/pause")
    public ResponseEntity<PauseTournamentResponse> pause(
            @PathVariable String id,
            @Valid @RequestBody PauseTournamentRequest request) {
        Tournament updated = pauseTournamentUseCase.execute(new PauseTournamentCommand(id, request.action()));

        String message = updated.isPaused()
                ? "The tournament was successfully paused"
                : "The tournament was successfully resumed";

        return ResponseEntity.ok(new PauseTournamentResponse(
                updated.getId(), updated.getStatus(), updated.isPaused(), message));
    }

    @Override
    @PatchMapping("/{id}/inactivate")
    public ResponseEntity<InactivateTournamentResponse> inactivate(
            @PathVariable String id,
            @Valid @RequestBody InactivateTournamentRequest request) {
        Tournament updated = inactivateTournamentUseCase.execute(new InactivateTournamentCommand(id, request.action()));

        String message = updated.isActive()
                ? "The tournament was successfully reactivated"
                : "The tournament was successfully inactivated";

        return ResponseEntity.ok(new InactivateTournamentResponse(
                updated.getId(), updated.getStatus(), updated.isActive(), message));
    }

    @Override
    @PatchMapping("/{tournamentId}/teams/{teamId}/disqualify")
    public ResponseEntity<DisqualifyTeamResponse> disqualifyTeam(
            @PathVariable String tournamentId,
            @PathVariable String teamId,
            @Valid @RequestBody DisqualifyTeamRequest request) {
        disqualifyTeamUseCase.disqualify(tournamentId, teamId, request.reason());

        return ResponseEntity.ok(new DisqualifyTeamResponse(
                tournamentId, teamId, RegistrationStatus.DISQUALIFIED,
                "The team was successfully disqualified"));
    }

    @Override
    @PatchMapping("/{tournamentId}/teams/{teamId}/inactivate")
    public ResponseEntity<InactivateTeamResponse> inactivateTeam(
            @PathVariable String tournamentId,
            @PathVariable String teamId) {
        inactivateTeamUseCase.inactivate(tournamentId, teamId);

        return ResponseEntity.ok(new InactivateTeamResponse(
                tournamentId, teamId, RegistrationStatus.INACTIVE,
                "The team was successfully inactivated"));
    }

    @Override
    @PatchMapping("/{tournamentId}/users/{userId}/inactivate")
    public ResponseEntity<InactivateUserResponse> inactivateUser(
            @PathVariable String tournamentId,
            @PathVariable String userId) {
        inactivateUserUseCase.inactivate(tournamentId, userId);

        return ResponseEntity.ok(new InactivateUserResponse(
                tournamentId, userId, ParticipantStatus.INACTIVE,
                "The user was successfully inactivated in the tournament"));
    }
}
