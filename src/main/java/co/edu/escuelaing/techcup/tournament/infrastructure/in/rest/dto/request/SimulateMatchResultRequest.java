package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request;

import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Body de {@code POST /sim/partidos/{matchId}/resultado} (perfil dev): mismos
 * campos que manda Matches en el evento {@code techcup.match.finished}, salvo
 * matchId/tournamentId (vienen de la URL / se resuelven por matchId) y
 * finishedAt (no se usa). Nombres en español, igual que el evento real.
 */
@Schema(description = "Simulated match result, dev profile only. Same shape as the techcup.match.finished event.")
public record SimulateMatchResultRequest(
        @NotNull @Schema(description = "Home team score.", example = "2") Integer golesA,
        @NotNull @Schema(description = "Away team score.", example = "1") Integer golesB,
        @Schema(description = "Winner team ID, or null if tied and unresolved (ELIMINATORIA pending penalties).")
        UUID ganadorId,
        @Schema(description = "Eliminated team ID (informational only; not used by processing).")
        UUID eliminadoId,
        @Schema(description = "Absent team ID: non-null means the match was a walkover (marked FINISHED_NO_SHOW).")
        UUID ausenteId,
        @NotNull @Schema(description = "GRUPOS or ELIMINATORIA.", example = "GRUPOS") MatchPhase fase
) {}
