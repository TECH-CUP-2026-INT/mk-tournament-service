package co.edu.escuelaing.techcup.tournament.domain.exception;

public class ScheduleConflictException extends RuntimeException {
    public ScheduleConflictException(String courtId, String refereeId) {
        super("La cancha '" + courtId + "' o el árbitro '" + refereeId
                + "' ya tienen un partido agendado en esa fecha y hora. "
                + "Seleccione otra fecha, hora, cancha o árbitro.");
    }
}
