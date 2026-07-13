package co.edu.escuelaing.techcup.tournament.controller.impl;

import co.edu.escuelaing.techcup.tournament.exception.InvalidCourtDataException;
import co.edu.escuelaing.techcup.tournament.service.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.CourtSection;
import co.edu.escuelaing.techcup.tournament.service.PreparationResult;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.ConsultHistoricalTournamentsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ConsultRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewRegisteredTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewMatchupsUseCase;
import co.edu.escuelaing.techcup.tournament.dto.response.RegisteredTeamResponse;
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
import co.edu.escuelaing.techcup.tournament.dto.request.CreateTournamentRequest;
import co.edu.escuelaing.techcup.tournament.dto.request.EditTournamentRequest;
import co.edu.escuelaing.techcup.tournament.dto.response.ChampionResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.CourtResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.DeleteTournamentResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.PreparationResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.RulebookResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.TournamentResponse;
import co.edu.escuelaing.techcup.tournament.mapper.TournamentRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/tournaments")
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
    private final ViewRegisteredTeamsUseCase viewRegisteredTeams;
    private final EditTournamentUseCase editTournamentUseCase;
    private final ViewMatchupsUseCase viewMatchups;
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
                                 ViewRegisteredTeamsUseCase viewRegisteredTeams,
                                 EditTournamentUseCase editTournamentUseCase,
                                 ViewMatchupsUseCase viewMatchups,
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
        this.viewRegisteredTeams = viewRegisteredTeams;
        this.editTournamentUseCase = editTournamentUseCase;
        this.viewMatchups = viewMatchups;
        this.mapper = mapper;
    }

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

    @PatchMapping("/{id}/finalize")
    public ResponseEntity<TournamentResponse> finalize(@PathVariable String id) {
        Tournament finalized = finalizeTournamentUseCase.finalizeTournament(id);

        return ResponseEntity.ok(mapper.toResponse(finalized));
    }

    @GetMapping("/{tournamentId}/preparation")
    public ResponseEntity<PreparationResponse> checkPreparation(@PathVariable String tournamentId) {
        PreparationResult result = checkPreparation.check(tournamentId);
        String status = result.isReadyToActivate() ? "completo" : "incompleto";
        return ResponseEntity.ok(new PreparationResponse(status, result.isReadyToActivate(),
                result.getApprovedTeamsCount(), result.getMissingRequirements()));
    }

    @PostMapping("/{tournamentId}/matches/{matchId}/champion")
    public ResponseEntity<ChampionResponse> assignChampion(
            @PathVariable String tournamentId,
            @PathVariable String matchId) {
        ChampionAssignment assignment = assignChampionUseCase.assignChampion(tournamentId, matchId);
        return ResponseEntity.ok(new ChampionResponse(
                tournamentId, assignment.championTeamId(), assignment.resolution()));
    }

    @GetMapping("/{tournamentId}/champion")
    public ResponseEntity<ChampionResponse> getChampion(@PathVariable String tournamentId) {
        ChampionAssignment assignment = getChampionUseCase.getChampion(tournamentId);
        return ResponseEntity.ok(new ChampionResponse(
                tournamentId, assignment.championTeamId(), assignment.resolution()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteTournamentResponse> delete(@PathVariable String id) {
        deleteTournamentUseCase.delete(id);
        return ResponseEntity.ok(new DeleteTournamentResponse(
                "El torneo '" + id + "' ha sido eliminado permanentemente."
        ));
    }

    @GetMapping("/{tournamentId}/matchups")
    public ResponseEntity<java.util.List<MatchupResponse>> getMatchups(
            @PathVariable String tournamentId) {
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

    @GetMapping("/{tournamentId}/teams")
    public ResponseEntity<java.util.List<RegisteredTeamResponse>> getRegisteredTeams(
            @PathVariable String tournamentId) {
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

    @GetMapping("/history")
    public ResponseEntity<java.util.List<co.edu.escuelaing.techcup.tournament.dto.response.HistoricalTournamentResponse>> getHistory() {
        java.util.List<co.edu.escuelaing.techcup.tournament.dto.response.HistoricalTournamentResponse> result =
                consultHistorical.findAll().stream()
                        .map(this::toHistoricalResponse)
                        .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/{tournamentId}")
    public ResponseEntity<co.edu.escuelaing.techcup.tournament.dto.response.HistoricalTournamentResponse> getHistoricalById(
            @PathVariable String tournamentId) {
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

    @GetMapping("/{tournamentId}/rulebook")
    public ResponseEntity<org.springframework.core.io.InputStreamResource> consultRulebook(
            @PathVariable String tournamentId) {
        ConsultRulebookUseCase.RulebookResource resource = consultRulebook.consult(tournamentId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + resource.fileName() + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(new org.springframework.core.io.InputStreamResource(resource.content()));
    }

    @PostMapping("/{tournamentId}/rulebook")
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
                updated.getId(), updated.getRulebookFileId(), "Reglamento adjuntado correctamente"
        ));
    }

    @PostMapping("/{tournamentId}/courts")
    public ResponseEntity<CourtResponse> registerCourt(
            @PathVariable String tournamentId,
            @RequestParam("section") String section,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

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

    @PatchMapping("/{id}")
    public ResponseEntity<TournamentResponse> edit(@PathVariable String id, @Valid @RequestBody EditTournamentRequest request) {
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
}
