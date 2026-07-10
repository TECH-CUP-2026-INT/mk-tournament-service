package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TournamentCannotBeFinalizedException extends RuntimeException {
    public TournamentCannotBeFinalizedException(String message) {
        super(message);
    }
}
