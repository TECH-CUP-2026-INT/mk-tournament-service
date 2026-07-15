package co.edu.escuelaing.techcup.tournament.domain.exception;

public class InsufficientApprovedTeamsException extends RuntimeException {
    public InsufficientApprovedTeamsException(String message) {
        super(message);
    }
}
