package co.edu.escuelaing.techcup.tournament.exception;

public class TeamInactivationNotAllowedException extends RuntimeException {
    public TeamInactivationNotAllowedException(String message) {
        super(message);
    }
}
