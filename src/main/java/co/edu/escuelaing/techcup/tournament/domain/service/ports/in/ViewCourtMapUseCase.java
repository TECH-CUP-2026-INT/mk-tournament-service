package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;

import java.util.List;
import java.util.UUID;

public interface ViewCourtMapUseCase {

    List<CourtMapEntry> getCourtMap(UUID tournamentId);

    /**
     * Una entrada del mapa de canchas: la cancha, y —si ya tiene un partido
     * asignado— ese partido y su programación (fecha/hora). {@code match} y
     * {@code scheduledMatch} son {@code null} para una cancha todavía disponible.
     */
    record CourtMapEntry(Court court, Match match, ScheduledMatch scheduledMatch) {}
}
