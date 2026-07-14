package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Match;
import co.edu.escuelaing.techcup.tournament.service.MatchActivationAction;

public interface InactivateMatchUseCase {

    Match execute(InactivateMatchCommand command);

    record InactivateMatchCommand(String matchId, MatchActivationAction action) {}
}
