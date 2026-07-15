package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TeamInactivationNotAllowedException extends RuntimeException {
    public TeamInactivationNotAllowedException(String message) {
        super(message);
    }
}
