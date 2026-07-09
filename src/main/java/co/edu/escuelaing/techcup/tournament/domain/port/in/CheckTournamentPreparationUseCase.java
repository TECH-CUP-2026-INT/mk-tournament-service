package co.edu.escuelaing.techcup.tournament.domain.port.in;

import co.edu.escuelaing.techcup.tournament.domain.model.PreparationResult;

public interface CheckTournamentPreparationUseCase {
    PreparationResult check(String tournamentId);
}
