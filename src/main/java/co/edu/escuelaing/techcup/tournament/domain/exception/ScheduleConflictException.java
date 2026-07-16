package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class ScheduleConflictException extends RuntimeException {
    public ScheduleConflictException(UUID courtId, UUID refereeId) {
        super("La cancha '" + courtId + "' o el árbitro '" + refereeId
                + "' ya tienen un partido agendado en esa fecha y hora. "
                + "Seleccione otra fecha, hora, cancha o árbitro.");
    }
}
