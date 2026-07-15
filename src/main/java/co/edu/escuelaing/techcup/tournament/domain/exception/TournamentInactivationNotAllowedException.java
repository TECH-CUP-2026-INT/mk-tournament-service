package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TournamentInactivationNotAllowedException extends RuntimeException {
    public TournamentInactivationNotAllowedException(String message) {
        super(message);
    }
}
