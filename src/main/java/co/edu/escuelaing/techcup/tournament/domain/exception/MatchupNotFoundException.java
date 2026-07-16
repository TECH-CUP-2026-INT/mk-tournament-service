package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class MatchupNotFoundException extends RuntimeException {
    public MatchupNotFoundException(UUID matchupId) {
        super("No existe una matchup pairing con id '" + matchupId + "'");
    }
}
