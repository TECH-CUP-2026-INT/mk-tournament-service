package co.edu.escuelaing.techcup.tournament.infrastructure.in.messaging.dto;

import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;

import java.time.Instant;
import java.util.UUID;

/**
 * Espejo exacto del evento publicado por Matches en la routing key
 * {@code techcup.match.finished}. Nombres de campo en español porque así los
 * publica Matches (no son parte de nuestro dominio interno, ver
 * {@link co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ProcessMatchResultUseCase}
 * para el comando en inglés que sí usamos internamente).
 * <p>
 * {@code matchId} es el id que este servicio generó y envió en la definición
 * del partido (competenciaMatchId); {@code ganadorId}/{@code eliminadoId} ya
 * vienen resueltos por Matches, penales incluidos — este servicio no los
 * recalcula.
 */
public record MatchFinishedEvent(
        UUID matchId,
        UUID tournamentId,
        MatchPhase fase,
        int golesA,
        int golesB,
        UUID ganadorId,
        UUID eliminadoId,
        Instant finishedAt) {}
