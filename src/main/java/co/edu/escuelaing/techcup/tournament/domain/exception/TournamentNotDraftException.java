package co.edu.escuelaing.techcup.tournament.domain.exception;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

public class TournamentNotDraftException extends RuntimeException {

    public TournamentNotDraftException(String id, TournamentStatus currentStatus) {
        super("El torneo '" + id + "' no puede eliminarse porque su estado actual es: " + currentStatus
                + ". Only tournaments in DRAFT status can be deleted.");
    }
}
