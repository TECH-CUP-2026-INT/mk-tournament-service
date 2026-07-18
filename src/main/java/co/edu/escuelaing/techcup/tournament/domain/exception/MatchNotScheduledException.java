package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class MatchNotScheduledException extends RuntimeException {
    public MatchNotScheduledException(UUID matchId) {
        super("El partido '" + matchId + "' aún no ha sido programado (sin cancha/árbitro/fecha); "
                + "no hay definición que reenviar");
    }
}
