package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TournamentPauseNotAllowedException extends RuntimeException {
    public TournamentPauseNotAllowedException(String message) {
        super(message);
    }
}
