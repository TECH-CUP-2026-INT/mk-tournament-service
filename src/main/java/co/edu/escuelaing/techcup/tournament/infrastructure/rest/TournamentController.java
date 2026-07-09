package co.edu.escuelaing.techcup.tournament.infrastructure.rest;

import co.edu.escuelaing.techcup.tournament.domain.model.PreparationResult;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.port.in.CheckTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.domain.port.in.CreateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.port.in.RemoveTeamUseCase;
import co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    private final CreateTournamentUseCase createTournament;
    private final CheckTournamentPreparationUseCase checkPreparation;
    private final RemoveTeamUseCase removeTeam;

    public TournamentController(CreateTournamentUseCase createTournament,
                                CheckTournamentPreparationUseCase checkPreparation,
                                RemoveTeamUseCase removeTeam) {
        this.createTournament = createTournament;
        this.checkPreparation = checkPreparation;
        this.removeTeam = removeTeam;
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody TournamentRequest request) {
        Tournament tournament = new Tournament(null, request.name(), request.startDate(),
                request.endDate(), request.eliminationType());
        return ResponseEntity.status(201).body(toResponse(createTournament.create(tournament)));
    }

    @GetMapping("/{tournamentId}/preparation")
    public ResponseEntity<PreparationResponse> checkPreparation(@PathVariable String tournamentId) {
        PreparationResult result = checkPreparation.check(tournamentId);
        String status = result.isReadyToActivate() ? "completo" : "incompleto";
        return ResponseEntity.ok(new PreparationResponse(
                status,
                result.isReadyToActivate(),
                result.getApprovedTeamsCount(),
                result.getMissingRequirements()
        ));
    }

    @DeleteMapping("/{tournamentId}/teams/{teamId}")
    public ResponseEntity<TournamentResponse> removeTeam(
            @PathVariable String tournamentId,
            @PathVariable String teamId,
            @Valid @RequestBody RemoveTeamRequest request) {
        return ResponseEntity.ok(toResponse(removeTeam.remove(tournamentId, teamId, request.reason())));
    }

    private TournamentResponse toResponse(Tournament t) {
        List<TournamentResponse.TeamResponse> teams = t.getTeams() == null ? List.of() :
                t.getTeams().stream()
                        .map(tr -> new TournamentResponse.TeamResponse(
                                tr.getTeamId(), tr.getTeamName(), tr.getRegistrationStatus(), tr.getPoints()))
                        .toList();
        return new TournamentResponse(t.getId(), t.getName(), t.getStartDate(), t.getEndDate(),
                t.getDurationDays(), t.getStatus(), t.getEliminationType(), teams);
    }
}
