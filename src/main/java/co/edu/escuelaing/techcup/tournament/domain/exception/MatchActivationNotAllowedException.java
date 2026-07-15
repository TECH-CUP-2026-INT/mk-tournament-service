package co.edu.escuelaing.techcup.tournament.domain.exception;

public class MatchActivationNotAllowedException extends RuntimeException {
    public MatchActivationNotAllowedException(String message) {
        super(message);
    }
}
