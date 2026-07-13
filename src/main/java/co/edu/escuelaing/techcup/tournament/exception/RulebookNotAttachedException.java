package co.edu.escuelaing.techcup.tournament.exception;

public class RulebookNotAttachedException extends RuntimeException {

    public RulebookNotAttachedException(String tournamentId) {
        super("El torneo '" + tournamentId + "' aún no tiene un reglamento adjunto");
    }
}
