package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import java.util.UUID;

public interface ActivateTournamentUseCase {
    Tournament activate(UUID tournamentId);
}
