package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import java.util.Optional;

public interface ViewMatchCourtUseCase {
    Optional<Court> getCourtByMatch(String matchId);
}
