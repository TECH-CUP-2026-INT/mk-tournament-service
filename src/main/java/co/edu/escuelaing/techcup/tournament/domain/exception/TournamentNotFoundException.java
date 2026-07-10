// domain/exception/TournamentNotFoundException.java
package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TournamentNotFoundException extends RuntimeException {
    public TournamentNotFoundException(String tournamentId) {
        super("No existe un torneo con id " + tournamentId);
    }
}
