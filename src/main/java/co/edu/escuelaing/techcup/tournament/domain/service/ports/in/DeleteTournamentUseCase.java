package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import java.util.UUID;

public interface DeleteTournamentUseCase {

    void delete(UUID tournamentId);
}
