package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TeamRosterSizeInvalidException extends RuntimeException {
    public TeamRosterSizeInvalidException(String message) {
        super(message);
    }
}
