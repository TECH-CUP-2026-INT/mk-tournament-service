package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.BracketNodeStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Round;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "One node of the elimination bracket. slotA/slotB are null (\"To be defined\") until the "
        + "previous round resolves them.")
public record BracketNodeResponse(
        @Schema(description = "Node ID.") UUID nodeId,
        @Schema(description = "Round this node belongs to.", example = "SEMIFINAL") Round round,
        @Schema(description = "Team occupying slot A, or null if not yet resolved.") UUID slotA,
        @Schema(description = "Team occupying slot B, or null if not yet resolved.") UUID slotB,
        @Schema(description = "ID of the match created for this node once both slots are filled, or null.") UUID matchId,
        @Schema(description = "Node status.") BracketNodeStatus status,
        @Schema(description = "Winner, once resolved.") UUID winnerTeamId,
        @Schema(description = "Loser, once resolved.") UUID loserTeamId,
        @Schema(description = "ID of the next round's node this winner advances to, or null for the Final.") UUID advanceToNodeId
) {
    private static final String PENDING_SLOT = "To be defined";

    public String displaySlotA() {
        return slotA != null ? slotA.toString() : PENDING_SLOT;
    }

    public String displaySlotB() {
        return slotB != null ? slotB.toString() : PENDING_SLOT;
    }
}
