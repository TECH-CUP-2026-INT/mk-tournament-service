package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Match;

import java.util.List;

public interface ViewMatchesUseCase {
    List<Match> getMatches(String tournamentId);
}
