package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.PreparationResult;

public interface CheckTournamentPreparationUseCase {
    PreparationResult check(String tournamentId);
}
