package co.edu.escuelaing.techcup.tournament.domain.model;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidScheduledMatchDataException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Programación real de un partido: fecha, hora, cancha y árbitro asignados a un
 * enfrentamiento ({@link Match}) ya generado en el fixture.
 */
public class ScheduledMatch extends AggregateRoot {

    private final UUID matchupId;
    private final UUID courtId;
    private final UUID refereeId;
    private final LocalDate matchDate;
    private final LocalTime matchTime;

    private ScheduledMatch(UUID id, UUID matchupId, UUID courtId, UUID refereeId,
                           LocalDate matchDate, LocalTime matchTime) {
        super(id);
        this.matchupId = matchupId;
        this.courtId = courtId;
        this.refereeId = refereeId;
        this.matchDate = matchDate;
        this.matchTime = matchTime;
    }

    public static ScheduledMatch create(UUID matchupId, UUID courtId, UUID refereeId,
                                        LocalDate matchDate, LocalTime matchTime) {
        validateNotNull(matchupId, "La matchup pairing es obligatoria");
        validateNotNull(courtId, "La cancha es obligatoria");
        validateNotNull(refereeId, "El árbitro es obligatorio");
        if (matchDate == null)
            throw new InvalidScheduledMatchDataException("La fecha del partido es obligatoria");
        if (matchTime == null)
            throw new InvalidScheduledMatchDataException("La hora del partido es obligatoria");
        return new ScheduledMatch(UUID.randomUUID(), matchupId, courtId, refereeId, matchDate, matchTime);
    }

    public static ScheduledMatch reconstruct(UUID id, UUID matchupId, UUID courtId, UUID refereeId,
                                             LocalDate matchDate, LocalTime matchTime) {
        return new ScheduledMatch(id, matchupId, courtId, refereeId, matchDate, matchTime);
    }

    private static void validateNotNull(UUID value, String message) {
        if (value == null)
            throw new InvalidScheduledMatchDataException(message);
    }

    public UUID getMatchupId() { return matchupId; }
    public UUID getCourtId() { return courtId; }
    public UUID getRefereeId() { return refereeId; }
    public LocalDate getMatchDate() { return matchDate; }
    public LocalTime getMatchTime() { return matchTime; }
}
