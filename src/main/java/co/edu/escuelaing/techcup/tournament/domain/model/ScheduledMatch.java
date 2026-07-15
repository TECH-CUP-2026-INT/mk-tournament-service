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

    private final String matchupId;
    private final String courtId;
    private final String refereeId;
    private final LocalDate matchDate;
    private final LocalTime matchTime;

    private ScheduledMatch(String id, String matchupId, String courtId, String refereeId,
                           LocalDate matchDate, LocalTime matchTime) {
        super(id);
        this.matchupId = matchupId;
        this.courtId = courtId;
        this.refereeId = refereeId;
        this.matchDate = matchDate;
        this.matchTime = matchTime;
    }

    public static ScheduledMatch create(String matchupId, String courtId, String refereeId,
                                        LocalDate matchDate, LocalTime matchTime) {
        validateNotBlank(matchupId, "La matchup pairing es obligatoria");
        validateNotBlank(courtId, "La cancha es obligatoria");
        validateNotBlank(refereeId, "El árbitro es obligatorio");
        if (matchDate == null)
            throw new InvalidScheduledMatchDataException("La fecha del partido es obligatoria");
        if (matchTime == null)
            throw new InvalidScheduledMatchDataException("La hora del partido es obligatoria");
        return new ScheduledMatch(UUID.randomUUID().toString(), matchupId, courtId, refereeId, matchDate, matchTime);
    }

    public static ScheduledMatch reconstruct(String id, String matchupId, String courtId, String refereeId,
                                             LocalDate matchDate, LocalTime matchTime) {
        return new ScheduledMatch(id, matchupId, courtId, refereeId, matchDate, matchTime);
    }

    private static void validateNotBlank(String value, String message) {
        if (value == null || value.isBlank())
            throw new InvalidScheduledMatchDataException(message);
    }

    public String getMatchupId() { return matchupId; }
    public String getCourtId() { return courtId; }
    public String getRefereeId() { return refereeId; }
    public LocalDate getMatchDate() { return matchDate; }
    public LocalTime getMatchTime() { return matchTime; }
}
