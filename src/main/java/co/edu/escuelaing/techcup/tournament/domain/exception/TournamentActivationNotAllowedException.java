package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TournamentActivationNotAllowedException extends RuntimeException {
    public TournamentActivationNotAllowedException(String message) {
        super(message);
    }
}
