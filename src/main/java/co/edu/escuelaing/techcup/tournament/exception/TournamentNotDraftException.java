package co.edu.escuelaing.techcup.tournament.exception;

import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;

public class TournamentNotDraftException extends RuntimeException {

    public TournamentNotDraftException(String id, TournamentStatus currentStatus) {
        super("El torneo '" + id + "' no puede eliminarse porque su estado actual es: " + currentStatus
                + ". Only tournaments in DRAFT status can be deleted.");
    }
}
