package co.edu.escuelaing.techcup.tournament.domain.exception;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

import java.util.UUID;

public class TournamentCannotBeDeletedException extends RuntimeException {

    public TournamentCannotBeDeletedException(UUID id, TournamentStatus currentStatus) {
        super("El torneo '" + id + "' no puede eliminarse porque su estado actual es: " + currentStatus
                + ". Solo se pueden eliminar torneos en estado Finalizado.");
    }
}
