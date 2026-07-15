package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TournamentCannotBeEditedException extends RuntimeException {
    public TournamentCannotBeEditedException(String message) {
        super(message);
    }
}
