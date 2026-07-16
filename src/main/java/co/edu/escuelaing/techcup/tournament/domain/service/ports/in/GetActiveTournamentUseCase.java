package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import java.util.Optional;

/**
 * Returns the currently active tournament (status = ACTIVE), if any.
 * Used by external services (e.g. Statistics) to resolve the active tournament context.
 */
public interface GetActiveTournamentUseCase {
    Optional<Tournament> getActive();
}
