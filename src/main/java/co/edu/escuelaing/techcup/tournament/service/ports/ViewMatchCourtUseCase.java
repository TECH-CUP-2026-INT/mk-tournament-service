package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Court;
import java.util.Optional;

public interface ViewMatchCourtUseCase {
    Optional<Court> getCourtByMatch(String matchId);
}
