package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentInactivationAction;

import java.util.UUID;

public interface InactivateTournamentUseCase {

    Tournament execute(InactivateTournamentCommand command);

    record InactivateTournamentCommand(UUID tournamentId, TournamentInactivationAction action) {}
}
