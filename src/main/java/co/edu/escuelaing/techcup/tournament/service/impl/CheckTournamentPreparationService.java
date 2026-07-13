package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.service.PreparationResult;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.CheckTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CheckTournamentPreparationService implements CheckTournamentPreparationUseCase {

    private final TournamentRepositoryPort repository;

    public CheckTournamentPreparationService(TournamentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public PreparationResult check(String tournamentId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + tournamentId));
        tournament.assertActive();
        return tournament.checkPreparation();
    }
}
