package co.edu.escuelaing.techcup.tournament.entity.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchDocument {

    private String matchId;
    private String homeTeamId;
    private String awayTeamId;
    private String status;
    private boolean finalMatch;
    private int homeScore;
    private int awayScore;
    private String penaltyShootoutWinnerTeamId;
    // Boolean (no boolean primitivo): así los partidos guardados antes de esta
    // historia, que no tienen este campo en Mongo, se leen como null y se
    // tratan como activos en vez de caer por defecto en false (inactivos).
    private Boolean active;
}
