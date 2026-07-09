package co.edu.escuelaing.techcup.tournament.application.service;

import co.edu.escuelaing.techcup.tournament.domain.model.PreparationResult;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.port.in.CheckTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.domain.port.out.TournamentRepositoryPort;
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
        return tournament.checkPreparation();
    }
}
