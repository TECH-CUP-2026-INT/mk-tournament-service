package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Match;

import java.util.List;

public interface ViewMatchupsUseCase {

    List<Match> getMatchups(String tournamentId);
}
