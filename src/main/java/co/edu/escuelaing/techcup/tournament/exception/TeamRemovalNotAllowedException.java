package co.edu.escuelaing.techcup.tournament.exception;

public class TeamRemovalNotAllowedException extends RuntimeException {
    public TeamRemovalNotAllowedException(String message) {
        super(message);
    }
}
