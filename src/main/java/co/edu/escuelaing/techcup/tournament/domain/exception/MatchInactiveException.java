package co.edu.escuelaing.techcup.tournament.domain.exception;

public class MatchInactiveException extends RuntimeException {
    public MatchInactiveException(String message) {
        super(message);
    }
}
