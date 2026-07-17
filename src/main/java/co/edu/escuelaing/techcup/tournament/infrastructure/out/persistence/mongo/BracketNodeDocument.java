package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BracketNodeDocument {

    private UUID nodeId;
    private String round;
    private UUID slotA;
    private UUID slotB;
    private UUID matchId;
    private String status;
    private UUID winnerTeamId;
    private UUID loserTeamId;
    private UUID advanceToNodeId;
    private String advanceToSlot;
}
