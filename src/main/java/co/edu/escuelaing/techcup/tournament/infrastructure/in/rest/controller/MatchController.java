package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.MatchActivationRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.ScheduleMatchRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.MatchActivationResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.ScheduledMatchResponse;
import co.edu.escuelaing.techcup.tournament.application.mapper.ScheduledMatchRestMapper;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateMatchUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.InactivateMatchUseCase.InactivateMatchCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ScheduleMatchUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ScheduleMatchUseCase.ScheduleMatchCommand;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller.swagger.MatchControllerSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController implements MatchControllerSwagger {

    private final ScheduleMatchUseCase scheduleMatchUseCase;
    private final InactivateMatchUseCase inactivateMatchUseCase;
    private final ScheduledMatchRestMapper mapper;

    @Override
    @PostMapping
    public ResponseEntity<ScheduledMatchResponse> schedule(@Valid @RequestBody ScheduleMatchRequest request) {
        ScheduledMatch scheduled = scheduleMatchUseCase.schedule(new ScheduleMatchCommand(
                request.matchupId(), request.matchDate(), request.matchTime(),
                request.courtId(), request.refereeId()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(scheduled));
    }

    @Override
    @PatchMapping("/{matchId}/activation")
    public ResponseEntity<MatchActivationResponse> activation(
            @PathVariable String matchId,
            @Valid @RequestBody MatchActivationRequest request) {
        Match match = inactivateMatchUseCase.execute(new InactivateMatchCommand(matchId, request.action()));

        String message = match.isActive()
                ? "The match was successfully reactivated"
                : "The match was successfully inactivated";

        return ResponseEntity.ok(new MatchActivationResponse(match.getMatchId(), match.isActive(), message));
    }
}
