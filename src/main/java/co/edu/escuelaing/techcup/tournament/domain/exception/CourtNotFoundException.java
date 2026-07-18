package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class CourtNotFoundException extends RuntimeException {
    public CourtNotFoundException(UUID courtId) {
        super("No existe una cancha con id '" + courtId + "'");
    }
}
