package co.edu.escuelaing.techcup.tournament.exception;

public class ChampionPendingPenaltiesException extends RuntimeException {

    public ChampionPendingPenaltiesException(String matchId) {
        super("El partido final '" + matchId + "' terminó empatado en tiempo reglamentario. "
                + "Se requiere el resultado de la tanda de penales para asignar al campeón.");
    }
}
