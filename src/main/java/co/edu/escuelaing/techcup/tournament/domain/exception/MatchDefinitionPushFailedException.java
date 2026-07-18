package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class MatchDefinitionPushFailedException extends RuntimeException {
    public MatchDefinitionPushFailedException(UUID matchId, Throwable cause) {
        super("No se pudo enviar la definición del partido '" + matchId + "' a Matches", cause);
    }
}
