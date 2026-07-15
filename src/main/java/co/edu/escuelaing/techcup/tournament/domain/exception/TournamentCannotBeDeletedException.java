package co.edu.escuelaing.techcup.tournament.domain.exception;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

public class TournamentCannotBeDeletedException extends RuntimeException {

    public TournamentCannotBeDeletedException(String id, TournamentStatus currentStatus) {
        super("El torneo '" + id + "' no puede eliminarse porque su estado actual es: " + currentStatus
                + ". Solo se pueden eliminar torneos en estado Finalizado.");
    }
}
