package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentPauseAction;

public interface PauseTournamentUseCase {

    Tournament execute(PauseTournamentCommand command);

    record PauseTournamentCommand(String tournamentId, TournamentPauseAction action) {}
}
