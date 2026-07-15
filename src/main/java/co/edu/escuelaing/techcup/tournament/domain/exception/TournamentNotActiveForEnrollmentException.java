package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TournamentNotActiveForEnrollmentException extends RuntimeException {
    public TournamentNotActiveForEnrollmentException(String message) {
        super(message);
    }
}
