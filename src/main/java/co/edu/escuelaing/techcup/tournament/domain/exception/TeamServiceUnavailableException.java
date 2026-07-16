package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class TeamServiceUnavailableException extends RuntimeException {
    public TeamServiceUnavailableException(UUID teamId, Throwable cause) {
        super("No se pudo consultar la información del equipo '" + teamId
                + "' en el Team Service. Intente nuevamente.", cause);
    }
}
