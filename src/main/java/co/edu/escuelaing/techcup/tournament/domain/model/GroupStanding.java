package co.edu.escuelaing.techcup.tournament.domain.model;

import java.util.UUID;

/**
 * Fila de la tabla de posiciones de un grupo, ya ordenada (posición 1..N).
 * Ver {@link GroupStandingsCalculator} para cómo se calcula y ordena.
 */
public record GroupStanding(
        int position,
        UUID teamId,
        int played,
        int won,
        int drawn,
        int lost,
        int goalsFor,
        int goalsAgainst,
        int goalDifference,
        int points
) {
    GroupStanding withPosition(int newPosition) {
        return new GroupStanding(newPosition, teamId, played, won, drawn, lost,
                goalsFor, goalsAgainst, goalDifference, points);
    }
}
