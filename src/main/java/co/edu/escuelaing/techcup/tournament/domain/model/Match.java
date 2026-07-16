package co.edu.escuelaing.techcup.tournament.domain.model;

import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchActivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchInactiveException;

import java.util.UUID;

/**
 * Enfrentamiento generado en el fixture del torneo (quién juega contra quién),
 * con su resultado y estado. No incluye fecha/cancha/árbitro — eso lo modela
 * {@link ScheduledMatch}.
 */
public class Match {

    private UUID matchId;
    private UUID homeTeamId;
    private UUID awayTeamId;
    private MatchStatus status;
    private boolean finalMatch;
    private int homeScore;
    private int awayScore;
    private UUID penaltyShootoutWinnerTeamId;
    private boolean active = true;

    public Match() {}

    public Match(UUID matchId, UUID homeTeamId, UUID awayTeamId, MatchStatus status) {
        this(matchId, homeTeamId, awayTeamId, status, false, 0, 0, null, true);
    }

    public Match(UUID matchId, UUID homeTeamId, UUID awayTeamId, MatchStatus status,
                 boolean finalMatch, int homeScore, int awayScore, UUID penaltyShootoutWinnerTeamId) {
        this(matchId, homeTeamId, awayTeamId, status, finalMatch, homeScore, awayScore,
                penaltyShootoutWinnerTeamId, true);
    }

    public Match(UUID matchId, UUID homeTeamId, UUID awayTeamId, MatchStatus status,
                 boolean finalMatch, int homeScore, int awayScore, UUID penaltyShootoutWinnerTeamId,
                 boolean active) {
        this.matchId = matchId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.status = status;
        this.finalMatch = finalMatch;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.penaltyShootoutWinnerTeamId = penaltyShootoutWinnerTeamId;
        this.active = active;
    }

    public boolean involvesteam(UUID teamId) {
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

    public boolean isActive() {
        return active;
    }

    /**
     * Bloquea el registro de nuevos eventos de árbitro (marcador, penales,
     * no-show) sobre un partido inactivo, preservando los datos ya
     * registrados. Tarjetas, sustituciones y manejo del reloj no se
     * modelan en este servicio: son responsabilidad del Servicio de
     * Partidos, que debe consultar este estado antes de aceptar esos
     * eventos (dependencia pendiente).
     */
    public void assertActive() {
        if (!active) {
            throw new MatchInactiveException(
                    "El partido está inactivo, no se pueden registrar eventos de árbitro sobre él");
        }
    }

    public void inactivate() {
        if (!active) {
            throw new MatchActivationNotAllowedException("El partido ya está inactivo");
        }
        this.active = false;
    }

    public void reactivate() {
        if (active) {
            throw new MatchActivationNotAllowedException("El partido ya está activo");
        }
        this.active = true;
    }

    public void markAsNoShow() {
        assertActive();
        this.status = MatchStatus.FINISHED_NO_SHOW;
    }

    /**
     * Marca el partido como finalizado con el marcador en tiempo reglamentario.
     * Los penales no se suman al marcador; se registran por separado.
     */
    public void finish(int homeScore, int awayScore) {
        assertActive();
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
    public void recordPenaltyShootoutWinner(UUID winnerTeamId) {
        assertActive();
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

    public UUID resolveChampionTeamId() {
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

    public UUID getMatchId() { return matchId; }
    public void setMatchId(UUID matchId) { this.matchId = matchId; }

    public UUID getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(UUID homeTeamId) { this.homeTeamId = homeTeamId; }

    public UUID getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(UUID awayTeamId) { this.awayTeamId = awayTeamId; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public void setFinalMatch(boolean finalMatch) { this.finalMatch = finalMatch; }

    public int getHomeScore() { return homeScore; }
    public void setHomeScore(int homeScore) { this.homeScore = homeScore; }

    public int getAwayScore() { return awayScore; }
    public void setAwayScore(int awayScore) { this.awayScore = awayScore; }

    public UUID getPenaltyShootoutWinnerTeamId() { return penaltyShootoutWinnerTeamId; }
    public void setPenaltyShootoutWinnerTeamId(UUID penaltyShootoutWinnerTeamId) {
        this.penaltyShootoutWinnerTeamId = penaltyShootoutWinnerTeamId;
    }
}
