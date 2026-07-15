package co.edu.escuelaing.techcup.tournament.domain.exception;

public class NoAvailableSlotsException extends RuntimeException {
    public NoAvailableSlotsException(String message) {
        super(message);
    }
}
