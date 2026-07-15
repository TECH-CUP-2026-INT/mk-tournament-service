package co.edu.escuelaing.techcup.tournament.domain.model;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidSanctionDataException;

import java.util.UUID;

/**
 * Sanción aplicada a un jugador (tarjeta roja, amarillas acumuladas o conducta),
 * con los partidos de suspensión restantes.
 */
public class PlayerSanction extends AggregateRoot {

    private static final int AUTOMATIC_SANCTION_MATCHES = 1;

    private final String playerId;
    private final SanctionType type;
    private int matchesRemaining;

    private PlayerSanction(String id, String playerId, SanctionType type, int matchesRemaining) {
        super(id);
        this.playerId = playerId;
        this.type = type;
        this.matchesRemaining = matchesRemaining;
    }

    public static PlayerSanction create(String playerId, SanctionType type, Integer matchesSuspended) {
        if (playerId == null || playerId.isBlank())
            throw new InvalidSanctionDataException("El id del jugador es obligatorio");
        if (type == null)
            throw new InvalidSanctionDataException("El tipo de sanción es obligatorio");

        int matches = switch (type) {
            case RED_CARD, YELLOW_CARD_ACCUMULATION -> AUTOMATIC_SANCTION_MATCHES;
            case CONDUCT -> {
                if (matchesSuspended == null || matchesSuspended <= 0)
                    throw new InvalidSanctionDataException(
                            "El número de partidos suspendidos es obligatorio y debe ser mayor a 0 para sanciones por Conducta");
                yield matchesSuspended;
            }
        };

        return new PlayerSanction(UUID.randomUUID().toString(), playerId, type, matches);
    }

    public static PlayerSanction reconstruct(String id, String playerId, SanctionType type, int matchesRemaining) {
        return new PlayerSanction(id, playerId, type, matchesRemaining);
    }

    /**
     * Descuenta un partido de la sanción. Se invoca cuando finaliza un partido
     * cualquiera (no hay seguimiento de alineación/asistencia en el sistema,
     * decisión explícita: cualquier partido finalizado cuenta para todas las
     * sanciones activas).
     */
    public void serveMatch() {
        if (matchesRemaining > 0) matchesRemaining--;
    }

    public boolean isActive() {
        return matchesRemaining > 0;
    }

    public String getPlayerId() { return playerId; }
    public SanctionType getType() { return type; }
    public int getMatchesRemaining() { return matchesRemaining; }
}
