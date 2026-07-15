package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TeamDisqualificationNotAllowedException extends RuntimeException {
    public TeamDisqualificationNotAllowedException(String message) {
        super(message);
    }
}
