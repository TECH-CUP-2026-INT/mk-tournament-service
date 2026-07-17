package co.edu.escuelaing.techcup.tournament.domain.model;

import java.util.UUID;

/**
 * Nodo de la llave eliminatoria: un cruce entre dos cupos (slotA/slotB), que
 * pueden estar "Por definir" (null) hasta que la ronda previa los resuelve.
 * Una vez los dos cupos están llenos se crea el {@link Match} correspondiente
 * (matchId). {@code advanceToNodeId}/{@code advanceToSlot} apuntan al nodo de
 * la siguiente ronda al que avanza el ganador; ambos son null solo para el
 * nodo de la Final.
 */
public class BracketNode {

    private UUID nodeId;
    private Round round;
    private UUID slotA;
    private UUID slotB;
    private UUID matchId;
    private BracketNodeStatus status;
    private UUID winnerTeamId;
    private UUID loserTeamId;
    private UUID advanceToNodeId;
    private BracketSlot advanceToSlot;

    private BracketNode() {}

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID nodeId;
        private Round round;
        private UUID slotA;
        private UUID slotB;
        private UUID advanceToNodeId;
        private BracketSlot advanceToSlot;

        private Builder() {}

        public Builder nodeId(UUID nodeId) { this.nodeId = nodeId; return this; }
        public Builder round(Round round) { this.round = round; return this; }
        public Builder slotA(UUID slotA) { this.slotA = slotA; return this; }
        public Builder slotB(UUID slotB) { this.slotB = slotB; return this; }
        public Builder advanceToNodeId(UUID advanceToNodeId) { this.advanceToNodeId = advanceToNodeId; return this; }
        public Builder advanceToSlot(BracketSlot advanceToSlot) { this.advanceToSlot = advanceToSlot; return this; }

        public BracketNode build() {
            BracketNode node = new BracketNode();
            node.nodeId = nodeId;
            node.round = round;
            node.slotA = slotA;
            node.slotB = slotB;
            node.advanceToNodeId = advanceToNodeId;
            node.advanceToSlot = advanceToSlot;
            node.status = slotA != null && slotB != null
                    ? BracketNodeStatus.SCHEDULED
                    : BracketNodeStatus.PENDING_SLOTS;
            return node;
        }
    }

    /**
     * Reconstrucción desde persistencia: a diferencia del builder de creación,
     * respeta el estado y resultado ya guardados en vez de derivarlos.
     */
    public static BracketNode reconstruct(UUID nodeId, Round round, UUID slotA, UUID slotB, UUID matchId,
                                           BracketNodeStatus status, UUID winnerTeamId, UUID loserTeamId,
                                           UUID advanceToNodeId, BracketSlot advanceToSlot) {
        BracketNode node = new BracketNode();
        node.nodeId = nodeId;
        node.round = round;
        node.slotA = slotA;
        node.slotB = slotB;
        node.matchId = matchId;
        node.status = status;
        node.winnerTeamId = winnerTeamId;
        node.loserTeamId = loserTeamId;
        node.advanceToNodeId = advanceToNodeId;
        node.advanceToSlot = advanceToSlot;
        return node;
    }

    public boolean isFinal() {
        return advanceToNodeId == null;
    }

    public boolean hasBothSlots() {
        return slotA != null && slotB != null;
    }

    /**
     * Llena el cupo del ganador que avanza desde una ronda previa. No crea el
     * partido todavía: eso lo decide el llamador (ver {@link Tournament}) una
     * vez confirma que ambos cupos quedaron llenos.
     */
    public void fillSlot(BracketSlot slot, UUID teamId) {
        if (slot == BracketSlot.A) {
            this.slotA = teamId;
        } else {
            this.slotB = teamId;
        }
    }

    /**
     * Enlaza este nodo con el de la ronda siguiente al que avanza su ganador.
     * Se usa durante la construcción del árbol (ver Tournament#generateEliminationBracket),
     * cuando el nodo destino recién se acaba de crear y aún no existía al
     * construir este nodo.
     */
    public void setAdvanceTo(UUID nodeId, BracketSlot slot) {
        this.advanceToNodeId = nodeId;
        this.advanceToSlot = slot;
    }

    public void assignMatch(UUID matchId) {
        this.matchId = matchId;
        this.status = BracketNodeStatus.SCHEDULED;
    }

    public void markPendingPenalties() {
        this.status = BracketNodeStatus.PENDING_PENALTIES;
    }

    public void resolve(UUID winnerTeamId, UUID loserTeamId) {
        this.winnerTeamId = winnerTeamId;
        this.loserTeamId = loserTeamId;
        this.status = BracketNodeStatus.FINISHED;
    }

    public UUID getNodeId() { return nodeId; }
    public Round getRound() { return round; }
    public UUID getSlotA() { return slotA; }
    public UUID getSlotB() { return slotB; }
    public UUID getMatchId() { return matchId; }
    public BracketNodeStatus getStatus() { return status; }
    public UUID getWinnerTeamId() { return winnerTeamId; }
    public UUID getLoserTeamId() { return loserTeamId; }
    public UUID getAdvanceToNodeId() { return advanceToNodeId; }
    public BracketSlot getAdvanceToSlot() { return advanceToSlot; }
}
