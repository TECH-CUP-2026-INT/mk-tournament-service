package co.edu.escuelaing.techcup.tournament.infrastructure.rest;

import co.edu.escuelaing.techcup.tournament.domain.model.PreparationResult;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.port.in.CheckTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.domain.port.in.CreateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.port.in.DeleteTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.port.in.RemoveTeamUseCase;
import co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto.*;
import co.edu.escuelaing.techcup.tournament.infrastructure.rest.mapper.TournamentRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    private final CreateTournamentUseCase createTournamentUseCase;
    private final CheckTournamentPreparationUseCase checkPreparation;
    private final RemoveTeamUseCase removeTeam;
    private final DeleteTournamentUseCase deleteTournamentUseCase;
    private final TournamentRestMapper mapper;

    public TournamentController(CreateTournamentUseCase createTournamentUseCase,
                                CheckTournamentPreparationUseCase checkPreparation,
                                RemoveTeamUseCase removeTeam,
                                DeleteTournamentUseCase deleteTournamentUseCase,
                                TournamentRestMapper mapper) {
        this.createTournamentUseCase = createTournamentUseCase;
        this.checkPreparation = checkPreparation;
        this.removeTeam = removeTeam;
        this.deleteTournamentUseCase = deleteTournamentUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody CreateTournamentRequest request) {
        Tournament newTournament = Tournament.create(
                request.name(),
                request.numberOfTeams(),
                request.cost(),
                request.startDate(),
                request.endDate(),
                request.registrationDeadline()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(createTournamentUseCase.create(newTournament)));
    }

    @GetMapping("/{tournamentId}/preparation")
    public ResponseEntity<PreparationResponse> checkPreparation(@PathVariable String tournamentId) {
        PreparationResult result = checkPreparation.check(tournamentId);
        String status = result.isReadyToActivate() ? "completo" : "incompleto";
        return ResponseEntity.ok(new PreparationResponse(status, result.isReadyToActivate(),
                result.getApprovedTeamsCount(), result.getMissingRequirements()));
    }

    @DeleteMapping("/{tournamentId}/teams/{teamId}")
    public ResponseEntity<TournamentResponse> removeTeam(
            @PathVariable String tournamentId,
            @PathVariable String teamId,
            @Valid @RequestBody RemoveTeamRequest request) {
        return ResponseEntity.ok(mapper.toResponse(removeTeam.remove(tournamentId, teamId, request.reason())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteTournamentResponse> delete(@PathVariable String id) {
        deleteTournamentUseCase.delete(id);
        return ResponseEntity.ok(new DeleteTournamentResponse(
                "El torneo '" + id + "' ha sido eliminado permanentemente."
        ));
    }
}
