package co.edu.escuelaing.techcup.tournament.domain.exception;

public class UserInactivationNotAllowedException extends RuntimeException {
    public UserInactivationNotAllowedException(String message) {
        super(message);
    }
}
