package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.GroupTable;

import java.util.List;
import java.util.UUID;

public interface ViewGroupStandingsUseCase {
    List<GroupTable> getStandings(UUID tournamentId);
}
