package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class ChampionPendingPenaltiesException extends RuntimeException {

    public ChampionPendingPenaltiesException(UUID matchId) {
        super("El partido final '" + matchId + "' terminó empatado en tiempo reglamentario. "
                + "Se requiere el resultado de la tanda de penales para asignar al campeón.");
    }
}
