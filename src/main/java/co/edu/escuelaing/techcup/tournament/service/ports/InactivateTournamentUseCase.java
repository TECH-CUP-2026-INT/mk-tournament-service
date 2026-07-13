package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentInactivationAction;

public interface InactivateTournamentUseCase {

    Tournament execute(InactivateTournamentCommand command);

    record InactivateTournamentCommand(String tournamentId, TournamentInactivationAction action) {}
}
