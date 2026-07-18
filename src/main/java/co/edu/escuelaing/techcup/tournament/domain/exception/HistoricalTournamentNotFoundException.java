package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class HistoricalTournamentNotFoundException extends RuntimeException {

    public HistoricalTournamentNotFoundException(UUID tournamentId) {
        super("No se encontró un torneo finalizado con id '" + tournamentId + "'");
    }
}
