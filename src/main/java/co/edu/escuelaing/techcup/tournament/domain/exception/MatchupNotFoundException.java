package co.edu.escuelaing.techcup.tournament.domain.exception;

public class MatchupNotFoundException extends RuntimeException {
    public MatchupNotFoundException(String matchupId) {
        super("No existe una matchup pairing con id '" + matchupId + "'");
    }
}
