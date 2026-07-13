package co.edu.escuelaing.techcup.tournament.exception;

public class TournamentInactivationNotAllowedException extends RuntimeException {
    public TournamentInactivationNotAllowedException(String message) {
        super(message);
    }
}
