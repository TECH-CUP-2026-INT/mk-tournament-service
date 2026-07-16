package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class FixtureGenerationFailedException extends RuntimeException {
    public FixtureGenerationFailedException(UUID tournamentId, Throwable cause) {
        super("No se pudo generar el fixture para el torneo '" + tournamentId
                + "'. Intente nuevamente.", cause);
    }
}
