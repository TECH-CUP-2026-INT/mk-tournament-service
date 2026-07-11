package co.edu.escuelaing.techcup.tournament.exception;

public class MatchNotFoundException extends RuntimeException {

    public MatchNotFoundException(String tournamentId, String matchId) {
        super("No existe el partido '" + matchId + "' en el torneo '" + tournamentId + "'");
    }
}
