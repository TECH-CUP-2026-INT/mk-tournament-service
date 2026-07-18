package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class MatchNotFoundException extends RuntimeException {

    public MatchNotFoundException(UUID tournamentId, UUID matchId) {
        super("No existe el partido '" + matchId + "' en el torneo '" + tournamentId + "'");
    }
}
