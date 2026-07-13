package co.edu.escuelaing.techcup.tournament.exception;

public class TournamentPauseNotAllowedException extends RuntimeException {
    public TournamentPauseNotAllowedException(String message) {
        super(message);
    }
}
