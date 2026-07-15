package co.edu.escuelaing.techcup.tournament.domain.exception;

public class CourtNotFoundException extends RuntimeException {
    public CourtNotFoundException(String courtId) {
        super("No existe una cancha con id '" + courtId + "'");
    }
}
