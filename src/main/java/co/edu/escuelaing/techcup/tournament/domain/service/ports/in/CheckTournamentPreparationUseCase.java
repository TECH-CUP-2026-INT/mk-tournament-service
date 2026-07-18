package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.PreparationResult;

import java.util.UUID;

public interface CheckTournamentPreparationUseCase {
    PreparationResult check(UUID tournamentId);
}
