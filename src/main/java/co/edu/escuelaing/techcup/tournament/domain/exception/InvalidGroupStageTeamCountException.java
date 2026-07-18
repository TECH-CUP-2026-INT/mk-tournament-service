package co.edu.escuelaing.techcup.tournament.domain.exception;

public class InvalidGroupStageTeamCountException extends RuntimeException {
    public InvalidGroupStageTeamCountException(String message) {
        super(message);
    }
}
