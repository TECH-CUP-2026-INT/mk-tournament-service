package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Tournament;

public interface CreateTournamentUseCase {
    Tournament create(Tournament newTournament);
}
