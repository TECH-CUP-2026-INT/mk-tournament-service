package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TournamentPreparationNotAllowedException extends RuntimeException {
    public TournamentPreparationNotAllowedException(String message) {
        super(message);
    }
}
