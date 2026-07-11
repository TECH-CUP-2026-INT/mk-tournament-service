package co.edu.escuelaing.techcup.tournament.service;

import co.edu.escuelaing.techcup.tournament.exception.ChampionAssignmentNotAllowedException;

public class Match {

    private String matchId;
    private String homeTeamId;
    private String awayTeamId;
    private MatchStatus status;
    private boolean finalMatch;
    private int homeScore;
    private int awayScore;
    private String penaltyShootoutWinnerTeamId;

    public Match() {}

    public Match(String matchId, String homeTeamId, String awayTeamId, MatchStatus status) {
        this(matchId, homeTeamId, awayTeamId, status, false, 0, 0, null);
    }

    public Match(String matchId, String homeTeamId, String awayTeamId, MatchStatus status,
                 boolean finalMatch, int homeScore, int awayScore, String penaltyShootoutWinnerTeamId) {
        this.matchId = matchId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.status = status;
        this.finalMatch = finalMatch;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.penaltyShootoutWinnerTeamId = penaltyShootoutWinnerTeamId;
    }

    public boolean involvesteam(String teamId) {
        return teamId.equals(homeTeamId) || teamId.equals(awayTeamId);
    }

    public boolean isPending() {
        return status == MatchStatus.PENDING;
    }

    public boolean isFinished() {
        return status == MatchStatus.FINISHED;
    }

    public boolean isFinalMatch() {
        return finalMatch;
    }

    public boolean isTiedInRegulation() {
        return homeScore == awayScore;
    }

    public void markAsNoShow() {
        this.status = MatchStatus.FINISHED_NO_SHOW;
    }

    /**
     * Marca el partido como finalizado con el marcador en tiempo reglamentario.
     * Los penales no se suman al marcador; se registran por separado.
     */
    public void finish(int homeScore, int awayScore) {
        if (!finalMatch) {
            throw new ChampionAssignmentNotAllowedException(
                    "Solo el partido marcado como Final puede finalizarse para asignar campeón");
        }
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = MatchStatus.FINISHED;
    }

    /**
     * Registra el ganador de la tanda de penales (integración pendiente con módulo de arbitraje).
     */
    public void recordPenaltyShootoutWinner(String winnerTeamId) {
        if (!isTiedInRegulation()) {
            throw new ChampionAssignmentNotAllowedException(
                    "La tanda de penales solo aplica cuando hay empate en tiempo reglamentario");
        }
        if (!involvesteam(winnerTeamId)) {
            throw new ChampionAssignmentNotAllowedException(
                    "El ganador de penales debe ser uno de los equipos del partido");
        }
        this.penaltyShootoutWinnerTeamId = winnerTeamId;
    }

    public String resolveChampionTeamId() {
        if (!isFinished()) {
            throw new ChampionAssignmentNotAllowedException(
                    "El partido debe estar finalizado para resolver al campeón");
        }
        if (homeScore > awayScore) {
            return homeTeamId;
        }
        if (awayScore > homeScore) {
            return awayTeamId;
        }
        if (penaltyShootoutWinnerTeamId == null) {
            return null;
        }
        return penaltyShootoutWinnerTeamId;
    }

    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }

    public String getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(String homeTeamId) { this.homeTeamId = homeTeamId; }

    public String getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(String awayTeamId) { this.awayTeamId = awayTeamId; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public void setFinalMatch(boolean finalMatch) { this.finalMatch = finalMatch; }

    public int getHomeScore() { return homeScore; }
    public void setHomeScore(int homeScore) { this.homeScore = homeScore; }

    public int getAwayScore() { return awayScore; }
    public void setAwayScore(int awayScore) { this.awayScore = awayScore; }

    public String getPenaltyShootoutWinnerTeamId() { return penaltyShootoutWinnerTeamId; }
    public void setPenaltyShootoutWinnerTeamId(String penaltyShootoutWinnerTeamId) {
        this.penaltyShootoutWinnerTeamId = penaltyShootoutWinnerTeamId;
    }
}
