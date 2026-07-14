package co.edu.escuelaing.techcup.tournament.exception;

import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;

public class TournamentCannotBeDeletedException extends RuntimeException {

    public TournamentCannotBeDeletedException(String id, TournamentStatus currentStatus) {
        super("El torneo '" + id + "' no puede eliminarse porque su estado actual es: " + currentStatus
                + ". Solo se pueden eliminar torneos en estado Finalizado.");
    }
}
