package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchActivationAction;

public interface InactivateMatchUseCase {

    Match execute(InactivateMatchCommand command);

    record InactivateMatchCommand(String matchId, MatchActivationAction action) {}
}
