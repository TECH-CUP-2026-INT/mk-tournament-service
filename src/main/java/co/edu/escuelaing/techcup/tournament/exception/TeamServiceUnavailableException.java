package co.edu.escuelaing.techcup.tournament.exception;

public class TeamServiceUnavailableException extends RuntimeException {
    public TeamServiceUnavailableException(String teamId, Throwable cause) {
        super("No se pudo consultar la información del equipo '" + teamId
                + "' en el Team Service. Intente nuevamente.", cause);
    }
}
