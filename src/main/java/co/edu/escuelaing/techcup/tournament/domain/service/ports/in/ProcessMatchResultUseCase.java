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
     * <p>
     * {@code absentTeamId} (ausenteId del evento) viene no-null cuando el
     * partido fue walkover: el equipo que no se presentó. En ese caso el
     * partido se marca FINISHED_NO_SHOW (no FINISHED) sin importar la fase;
     * en ELIMINATORIA, {@code winnerTeamId} ya identifica al presente, así que
     * la llave avanza igual.
     */
    record ProcessMatchResultCommand(
            UUID matchId,
            UUID tournamentId,
            MatchPhase phase,
            int homeScore,
            int awayScore,
            UUID winnerTeamId,
            UUID absentTeamId) {}
}
