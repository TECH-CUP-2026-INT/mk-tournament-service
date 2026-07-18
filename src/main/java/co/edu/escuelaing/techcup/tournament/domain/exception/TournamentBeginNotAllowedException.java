package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TournamentBeginNotAllowedException extends RuntimeException {
    public TournamentBeginNotAllowedException(String message) {
        super(message);
    }
}
