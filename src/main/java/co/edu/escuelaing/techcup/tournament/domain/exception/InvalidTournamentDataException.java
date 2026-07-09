// domain/exception/InvalidTournamentDataException.java
package co.edu.escuelaing.techcup.tournament.domain.exception;

public class InvalidTournamentDataException extends RuntimeException {
    public InvalidTournamentDataException(String message) {
        super(message);
    }
}