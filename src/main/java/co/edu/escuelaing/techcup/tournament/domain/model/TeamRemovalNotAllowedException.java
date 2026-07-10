package co.edu.escuelaing.techcup.tournament.domain.model;

public class TeamRemovalNotAllowedException extends RuntimeException {
    public TeamRemovalNotAllowedException(String message) {
        super(message);
    }
}
