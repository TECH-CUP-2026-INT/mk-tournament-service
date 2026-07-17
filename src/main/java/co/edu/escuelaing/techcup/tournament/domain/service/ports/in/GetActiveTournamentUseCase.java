package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

public interface GetActiveTournamentUseCase {
    Tournament getActiveTournament();
}
