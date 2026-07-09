package co.edu.escuelaing.techcup.tournament.domain.model;

public class Match {

    private String matchId;
    private String homeTeamId;
    private String awayTeamId;
    private MatchStatus status;

    public Match() {}

    public Match(String matchId, String homeTeamId, String awayTeamId, MatchStatus status) {
        this.matchId = matchId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.status = status;
    }

    public boolean involvesteam(String teamId) {
        return teamId.equals(homeTeamId) || teamId.equals(awayTeamId);
    }

    public boolean isPending() {
        return status == MatchStatus.PENDING;
    }

    public void markAsNoShow() {
        this.status = MatchStatus.FINISHED_NO_SHOW;
    }

    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }

    public String getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(String homeTeamId) { this.homeTeamId = homeTeamId; }

    public String getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(String awayTeamId) { this.awayTeamId = awayTeamId; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }
}
