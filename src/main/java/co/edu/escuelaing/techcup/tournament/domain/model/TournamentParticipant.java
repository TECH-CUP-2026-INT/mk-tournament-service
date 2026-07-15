package co.edu.escuelaing.techcup.tournament.domain.model;

import java.util.UUID;

/**
 * Participante (jugador) dentro de un torneo, independiente del equipo al que
 * pertenece.
 */
public class TournamentParticipant extends AggregateRoot {

    private final String tournamentId;
    private final String userId;
    private ParticipantStatus status;

    private TournamentParticipant(String id, String tournamentId, String userId, ParticipantStatus status) {
        super(id);
        this.tournamentId = tournamentId;
        this.userId = userId;
        this.status = status;
    }

    public static TournamentParticipant create(String tournamentId, String userId) {
        return new TournamentParticipant(UUID.randomUUID().toString(), tournamentId, userId, ParticipantStatus.ACTIVE);
    }

    public static TournamentParticipant reconstruct(String id, String tournamentId, String userId, ParticipantStatus status) {
        return new TournamentParticipant(id, tournamentId, userId, status);
    }

    public void inactivate() {
        this.status = ParticipantStatus.INACTIVE;
    }

    public boolean isInactive() {
        return this.status == ParticipantStatus.INACTIVE;
    }

    public String getTournamentId() { return tournamentId; }
    public String getUserId() { return userId; }
    public ParticipantStatus getStatus() { return status; }
}
