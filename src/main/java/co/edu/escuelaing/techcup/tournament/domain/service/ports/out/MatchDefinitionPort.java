package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Puerto de salida para empujar a Matches la definición de un partido ya
 * programado (cancha/árbitro/fecha/hora), para que pueda materializarlo y
 * arbitrarlo. Se dispara al programar el partido (ver ScheduleMatchService),
 * no al generarse el fixture: Matches hace upsert por matchId mientras el
 * partido siga SCHEDULED de su lado.
 * <p>
 * A diferencia de los eventos best-effort de {@link TournamentEventPublisherPort}
 * / {@link RecognitionAwardPort}, este envío es crítico — sin él Matches no
 * puede jugar el partido — así que las fallas no deben tragarse en silencio
 * (ver {@link co.edu.escuelaing.techcup.tournament.domain.model.Match#markDefinitionSyncPending()}
 * y {@code ResendMatchDefinitionService} para el reintento manual).
 */
public interface MatchDefinitionPort {

    /**
     * Envía (crea o actualiza) la definición del partido. Debe propagar la
     * falla como {@link co.edu.escuelaing.techcup.tournament.domain.exception.MatchDefinitionPushFailedException}
     * para cualquier caso que no sea "partido ya no editable" (409 — Matches
     * ya lo inició o finalizó de su lado, eso SÍ se trata como esperado y no
     * lanza).
     */
    void sendDefinition(MatchDefinition definition);

    /**
     * Avisa a Matches que un partido fue inactivado. Ruta pendiente de
     * confirmar con el equipo de Matches (¿DELETE /api/partidos/{matchId} o
     * reenvío de la definición con algún estado de "inactivo"?) — la
     * implementación no debe inventar un endpoint no acordado; hasta que se
     * confirme, solo deja constancia en el log de que quedó pendiente.
     */
    void notifyMatchInactivated(UUID matchId);

    /**
     * equipoA = home, equipoB = away. fase distingue GRUPOS de ELIMINATORIA
     * (a muerte súbita) — puede venir null para partidos de formatos
     * BRACKETS/LEAGUE planos que aún no la asignan (ver {@link co.edu.escuelaing.techcup.tournament.domain.model.Match}).
     */
    record MatchDefinition(
            UUID matchId,
            UUID tournamentId,
            MatchPhase fase,
            UUID equipoAId,
            UUID equipoBId,
            String equipoANombre,
            String equipoBNombre,
            LocalDate fecha,
            LocalTime hora,
            UUID arbitroId,
            UUID canchaId) {}
}
