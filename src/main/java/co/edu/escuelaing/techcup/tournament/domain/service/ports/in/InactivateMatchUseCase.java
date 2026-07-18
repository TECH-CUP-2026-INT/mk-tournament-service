package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchActivationAction;

import java.util.UUID;

public interface InactivateMatchUseCase {

    Match execute(InactivateMatchCommand command);

    record InactivateMatchCommand(UUID matchId, MatchActivationAction action) {}
}
