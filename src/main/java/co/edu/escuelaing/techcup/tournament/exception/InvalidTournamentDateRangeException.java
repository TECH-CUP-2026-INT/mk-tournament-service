// domain/exception/InvalidTournamentDateRangeException.java
package co.edu.escuelaing.techcup.tournament.exception;

public class InvalidTournamentDateRangeException extends RuntimeException {
    public InvalidTournamentDateRangeException(String message) {
        super(message);
    }
}