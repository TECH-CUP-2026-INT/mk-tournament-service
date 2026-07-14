package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentPauseAction;

public interface PauseTournamentUseCase {

    Tournament execute(PauseTournamentCommand command);

    record PauseTournamentCommand(String tournamentId, TournamentPauseAction action) {}
}
