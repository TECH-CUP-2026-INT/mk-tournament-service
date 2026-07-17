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
    // Grupo de fase de clasificatoria ("Grupo A", "Grupo B", ...) y jornada (1..3)
    // dentro de ese grupo. Ambos null para partidos que no pertenecen a un grupo
    // (formatos BRACKETS/LEAGUE, o partidos de la llave eliminatoria).
    private String groupName;
    private Integer matchday;
    // fase (GRUPOS/ELIMINATORIA) y torneo dueño del partido: obligatorios para poder
    // empujar la definición a Matches (fase le dice si es a muerte súbita) y para que
    // ProcessMatchResult enrute el resultado entrante. Solo se completan hoy para los
    // partidos de grupos y de la llave eliminatoria del flujo "mundial"; los formatos
    // BRACKETS/LEAGUE planos (RandomFixtureGenerationAdapter) aún no los asignan.
    private MatchPhase phase;
    private UUID tournamentId;

    public Match() {}

    public Match(UUID matchId, UUID homeTeamId, UUID awayTeamId, MatchStatus status) {
        this.matchId = matchId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.status = status;
        this.active = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Reemplaza a los constructores de 8/9 parámetros (S107): agrupa los
     * campos de resultado/estado que antes se pasaban posicionalmente.
     */
    public static final class Builder {
        private UUID matchId;
        private UUID homeTeamId;
        private UUID awayTeamId;
        private MatchStatus status;
        private boolean finalMatch;
        private int homeScore;
        private int awayScore;
        private UUID penaltyShootoutWinnerTeamId;
        private boolean active = true;
        private String groupName;
        private Integer matchday;
        private MatchPhase phase;
        private UUID tournamentId;

        private Builder() {}

        public Builder matchId(UUID matchId) { this.matchId = matchId; return this; }
        public Builder homeTeamId(UUID homeTeamId) { this.homeTeamId = homeTeamId; return this; }
        public Builder awayTeamId(UUID awayTeamId) { this.awayTeamId = awayTeamId; return this; }
        public Builder status(MatchStatus status) { this.status = status; return this; }
        public Builder finalMatch(boolean finalMatch) { this.finalMatch = finalMatch; return this; }
        public Builder homeScore(int homeScore) { this.homeScore = homeScore; return this; }
        public Builder awayScore(int awayScore) { this.awayScore = awayScore; return this; }
        public Builder penaltyShootoutWinnerTeamId(UUID penaltyShootoutWinnerTeamId) {
            this.penaltyShootoutWinnerTeamId = penaltyShootoutWinnerTeamId;
            return this;
        }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder groupName(String groupName) { this.groupName = groupName; return this; }
        public Builder matchday(Integer matchday) { this.matchday = matchday; return this; }
        public Builder phase(MatchPhase phase) { this.phase = phase; return this; }
        public Builder tournamentId(UUID tournamentId) { this.tournamentId = tournamentId; return this; }

        public Match build() {
            Match match = new Match();
            match.matchId = matchId;
            match.homeTeamId = homeTeamId;
            match.awayTeamId = awayTeamId;
            match.status = status;
            match.finalMatch = finalMatch;
            match.homeScore = homeScore;
            match.awayScore = awayScore;
            match.penaltyShootoutWinnerTeamId = penaltyShootoutWinnerTeamId;
            match.active = active;
            match.groupName = groupName;
            match.matchday = matchday;
            match.phase = phase;
            match.tournamentId = tournamentId;
            return match;
        }
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
     * Finaliza el partido con un resultado ya resuelto externamente (Matches, vía
     * evento {@code techcup.match.finished} o el modo simulación): a diferencia de
     * {@link #finish(int, int)}, no exige {@code finalMatch} (aplica a cualquier
     * partido de grupos o de la llave eliminatoria) y acepta el ganador ya resuelto.
     * Si el partido queda empatado en tiempo reglamentario y {@code winnerTeamId}
     * viene null, el partido queda finalizado pero sin ganador resuelto: pendiente
     * de que el organizador registre penales por {@link #recordPenaltyShootoutWinner}.
     */
    public void finishWithExternalResult(int homeScore, int awayScore, UUID winnerTeamId) {
        assertActive();
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = MatchStatus.FINISHED;
        if (winnerTeamId != null) {
            if (!involvesteam(winnerTeamId)) {
                throw new ChampionAssignmentNotAllowedException(
                        "El ganador debe ser uno de los equipos del partido");
            }
            if (isTiedInRegulation()) {
                this.penaltyShootoutWinnerTeamId = winnerTeamId;
            }
        }
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

    /**
     * Devuelve al equipo perdedor de este partido dado un ganador ya resuelto
     * (ver {@link #resolveChampionTeamId()}): el otro equipo del enfrentamiento.
     */
    public UUID resolveRunnerUpTeamId(UUID winnerTeamId) {
        return winnerTeamId.equals(homeTeamId) ? awayTeamId : homeTeamId;
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

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Integer getMatchday() { return matchday; }
    public void setMatchday(Integer matchday) { this.matchday = matchday; }

    public MatchPhase getPhase() { return phase; }
    public void setPhase(MatchPhase phase) { this.phase = phase; }

    public UUID getTournamentId() { return tournamentId; }
    public void setTournamentId(UUID tournamentId) { this.tournamentId = tournamentId; }
}
