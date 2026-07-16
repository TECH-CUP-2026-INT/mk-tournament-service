package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class RulebookNotAttachedException extends RuntimeException {

    public RulebookNotAttachedException(UUID tournamentId) {
        super("El torneo '" + tournamentId + "' aún no tiene un reglamento adjunto");
    }
}
