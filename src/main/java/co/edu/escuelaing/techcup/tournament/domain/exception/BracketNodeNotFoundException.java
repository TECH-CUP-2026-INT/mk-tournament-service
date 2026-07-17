package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class BracketNodeNotFoundException extends RuntimeException {

    public BracketNodeNotFoundException(String message) {
        super(message);
    }

    public static BracketNodeNotFoundException forMatch(UUID matchId) {
        return new BracketNodeNotFoundException(
                "No se encontró un nodo de la llave eliminatoria para el partido '" + matchId + "'");
    }
}
