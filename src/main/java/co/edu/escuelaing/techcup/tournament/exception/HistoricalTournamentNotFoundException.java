package co.edu.escuelaing.techcup.tournament.exception;

public class HistoricalTournamentNotFoundException extends RuntimeException {

    public HistoricalTournamentNotFoundException(String tournamentId) {
        super("No se encontró un torneo finalizado con id '" + tournamentId + "'");
    }
}
