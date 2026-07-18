package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.model.PreparationResult;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.CheckTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckTournamentPreparationService implements CheckTournamentPreparationUseCase {

    private final TournamentRepositoryPort repository;


    @Override
    public PreparationResult check(UUID tournamentId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + tournamentId));
        tournament.assertActive();
        return tournament.checkPreparation();
    }
}
