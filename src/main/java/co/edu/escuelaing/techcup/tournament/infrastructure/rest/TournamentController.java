// infrastructure/rest/TournamentController.java
package co.edu.escuelaing.techcup.tournament.infrastructure.rest;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.port.in.CreateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto.CreateTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto.TournamentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.rest.mapper.TournamentRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    private final CreateTournamentUseCase createTournamentUseCase;
    private final TournamentRestMapper mapper;

    public TournamentController(CreateTournamentUseCase createTournamentUseCase, TournamentRestMapper mapper) {
        this.createTournamentUseCase = createTournamentUseCase;
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

        Tournament created = createTournamentUseCase.create(newTournament);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }
}