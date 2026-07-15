package co.edu.escuelaing.techcup.tournament.domain.exception;

public class InvalidScheduledMatchDataException extends RuntimeException {
    public InvalidScheduledMatchDataException(String message) {
        super(message);
    }
}
