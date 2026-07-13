package co.edu.escuelaing.techcup.tournament.controller.impl;

import co.edu.escuelaing.techcup.tournament.dto.request.ScheduleMatchRequest;
import co.edu.escuelaing.techcup.tournament.dto.response.ScheduledMatchResponse;
import co.edu.escuelaing.techcup.tournament.service.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.service.ports.ScheduleMatchUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.ScheduleMatchUseCase.ScheduleMatchCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matches")
public class MatchController {

    private final ScheduleMatchUseCase scheduleMatchUseCase;

    public MatchController(ScheduleMatchUseCase scheduleMatchUseCase) {
        this.scheduleMatchUseCase = scheduleMatchUseCase;
    }

    @PostMapping
    public ResponseEntity<ScheduledMatchResponse> schedule(@Valid @RequestBody ScheduleMatchRequest request) {
        ScheduledMatch scheduled = scheduleMatchUseCase.schedule(new ScheduleMatchCommand(
                request.matchupId(), request.matchDate(), request.matchTime(),
                request.courtId(), request.refereeId()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(new ScheduledMatchResponse(
                scheduled.getId(), scheduled.getMatchupId(), scheduled.getCourtId(),
                scheduled.getRefereeId(), scheduled.getMatchDate(), scheduled.getMatchTime()
        ));
    }
}
