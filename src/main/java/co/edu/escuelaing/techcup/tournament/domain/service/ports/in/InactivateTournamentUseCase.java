package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentInactivationAction;

public interface InactivateTournamentUseCase {

    Tournament execute(InactivateTournamentCommand command);

    record InactivateTournamentCommand(String tournamentId, TournamentInactivationAction action) {}
}
