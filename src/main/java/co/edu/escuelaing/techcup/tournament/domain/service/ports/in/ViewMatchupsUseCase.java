package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;

import java.util.List;
import java.util.UUID;

public interface ViewMatchupsUseCase {

    List<Match> getMatchups(UUID tournamentId);
}
