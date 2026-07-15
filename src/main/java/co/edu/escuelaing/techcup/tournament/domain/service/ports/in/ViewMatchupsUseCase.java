package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;

import java.util.List;

public interface ViewMatchupsUseCase {

    List<Match> getMatchups(String tournamentId);
}
