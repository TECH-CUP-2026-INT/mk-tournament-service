package co.edu.escuelaing.techcup.tournament.domain.exception;

public class FixtureGenerationFailedException extends RuntimeException {
    public FixtureGenerationFailedException(String tournamentId, Throwable cause) {
        super("No se pudo generar el fixture para el torneo '" + tournamentId
                + "'. Intente nuevamente.", cause);
    }
}
