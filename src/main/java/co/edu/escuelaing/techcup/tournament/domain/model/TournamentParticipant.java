package co.edu.escuelaing.techcup.tournament.domain.model;

import java.util.UUID;

/**
 * Participante (jugador) dentro de un torneo, independiente del equipo al que
 * pertenece.
 */
public class TournamentParticipant extends AggregateRoot {

    private final UUID tournamentId;
    private final UUID userId;
    private ParticipantStatus status;

    private TournamentParticipant(UUID id, UUID tournamentId, UUID userId, ParticipantStatus status) {
        super(id);
        this.tournamentId = tournamentId;
        this.userId = userId;
        this.status = status;
    }

    public static TournamentParticipant create(UUID tournamentId, UUID userId) {
        return new TournamentParticipant(UUID.randomUUID(), tournamentId, userId, ParticipantStatus.ACTIVE);
    }

    public static TournamentParticipant reconstruct(UUID id, UUID tournamentId, UUID userId, ParticipantStatus status) {
        return new TournamentParticipant(id, tournamentId, userId, status);
    }

    public void inactivate() {
        this.status = ParticipantStatus.INACTIVE;
    }

    public boolean isInactive() {
        return this.status == ParticipantStatus.INACTIVE;
    }

    public UUID getTournamentId() { return tournamentId; }
    public UUID getUserId() { return userId; }
    public ParticipantStatus getStatus() { return status; }
}
