package co.edu.escuelaing.techcup.tournament.infrastructure.rest;

import co.edu.escuelaing.techcup.tournament.domain.port.in.DeleteTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto.DeleteTournamentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentController {

    private final DeleteTournamentUseCase deleteTournamentUseCase;

    public TournamentController(DeleteTournamentUseCase deleteTournamentUseCase) {
        this.deleteTournamentUseCase = deleteTournamentUseCase;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteTournamentResponse> delete(@PathVariable String id) {
        deleteTournamentUseCase.delete(id);
        return ResponseEntity.ok(new DeleteTournamentResponse(
                "El torneo '" + id + "' ha sido eliminado permanentemente."
        ));
    }
}
