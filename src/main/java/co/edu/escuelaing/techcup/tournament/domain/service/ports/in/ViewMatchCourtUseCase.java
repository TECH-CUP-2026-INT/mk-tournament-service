package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import java.util.Optional;
import java.util.UUID;

public interface ViewMatchCourtUseCase {
    Optional<Court> getCourtByMatch(UUID matchId);
}
