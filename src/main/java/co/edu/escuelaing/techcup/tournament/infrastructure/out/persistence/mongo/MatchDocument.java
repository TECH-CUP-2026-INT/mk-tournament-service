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
public class MatchDocument {

    private UUID matchId;
    private UUID homeTeamId;
    private UUID awayTeamId;
    private String status;
    private boolean finalMatch;
    private int homeScore;
    private int awayScore;
    private UUID penaltyShootoutWinnerTeamId;
    // Boolean (no boolean primitivo): así los partidos guardados antes de esta
    // historia, que no tienen este campo en Mongo, se leen como null y se
    // tratan como activos en vez de caer por defecto en false (inactivos).
    private Boolean active;
    private String groupName;
    private Integer matchday;
    private String phase;
    private UUID tournamentId;
}
