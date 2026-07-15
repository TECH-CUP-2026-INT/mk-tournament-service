package co.edu.escuelaing.techcup.tournament.domain.exception;

public class RulebookNotAttachedException extends RuntimeException {

    public RulebookNotAttachedException(String tournamentId) {
        super("El torneo '" + tournamentId + "' aún no tiene un reglamento adjunto");
    }
}
