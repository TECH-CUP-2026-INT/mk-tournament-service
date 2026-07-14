package co.edu.escuelaing.techcup.tournament.exception;

public class TournamentCannotBeFinalizedException extends RuntimeException {
    public TournamentCannotBeFinalizedException(String message) {
        super(message);
    }
}
