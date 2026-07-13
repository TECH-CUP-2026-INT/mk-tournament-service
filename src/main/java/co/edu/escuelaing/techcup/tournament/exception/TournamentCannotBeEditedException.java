package co.edu.escuelaing.techcup.tournament.exception;

public class TournamentCannotBeEditedException extends RuntimeException {
    public TournamentCannotBeEditedException(String message) {
        super(message);
    }
}
