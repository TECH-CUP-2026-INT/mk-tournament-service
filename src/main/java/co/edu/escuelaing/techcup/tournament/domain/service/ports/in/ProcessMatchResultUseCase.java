package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;

import java.util.UUID;

/**
 * Manejador único del resultado de un partido, sin importar si llega por el
 * listener de RabbitMQ ({@code techcup.match.finished}) o por el modo
 * simulación ({@code POST /sim/partidos/{matchId}/resultado}) — ambos caminos
 * deben construir el mismo {@link ProcessMatchResultCommand} y delegar aquí.
 */
public interface ProcessMatchResultUseCase {

    void process(ProcessMatchResultCommand command);

    /**
     * {@code winnerTeamId} viene null cuando el partido de ELIMINATORIA terminó
     * empatado y Matches no resolvió penales (no se asume que vaya a hacerlo).
     * El id del equipo eliminado no se recibe: {@code Tournament} lo deriva del
     * ganador y del propio partido (siempre es "el otro equipo").
     */
    record ProcessMatchResultCommand(
            UUID matchId,
            UUID tournamentId,
            MatchPhase phase,
            int homeScore,
            int awayScore,
            UUID winnerTeamId) {}
}
