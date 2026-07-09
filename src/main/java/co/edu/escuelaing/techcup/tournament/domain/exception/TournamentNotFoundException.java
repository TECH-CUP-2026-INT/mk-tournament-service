package co.edu.escuelaing.techcup.tournament.domain.exception;

public class TournamentNotFoundException extends RuntimeException {

    public TournamentNotFoundException(String id) {
        super("Torneo no encontrado con id: " + id);
    }
}
