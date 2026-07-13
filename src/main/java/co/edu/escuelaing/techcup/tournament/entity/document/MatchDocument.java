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
}
