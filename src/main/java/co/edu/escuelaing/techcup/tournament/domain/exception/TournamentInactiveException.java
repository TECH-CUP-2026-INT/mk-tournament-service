package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TournamentInactiveException extends RuntimeException {
    public TournamentInactiveException(String message) {
        super(message);
    }
}
