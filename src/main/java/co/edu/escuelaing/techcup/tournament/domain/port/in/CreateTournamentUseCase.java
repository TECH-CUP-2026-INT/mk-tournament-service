// domain/port/in/CreateTournamentUseCase.java
package co.edu.escuelaing.techcup.tournament.domain.port.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

public interface CreateTournamentUseCase {
    Tournament create(Tournament newTournament);
}