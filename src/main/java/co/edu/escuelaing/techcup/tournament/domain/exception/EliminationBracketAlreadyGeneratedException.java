package co.edu.escuelaing.techcup.tournament.domain.exception;

public class EliminationBracketAlreadyGeneratedException extends RuntimeException {
    public EliminationBracketAlreadyGeneratedException(String message) {
        super(message);
    }
}
