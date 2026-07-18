package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GenerateEliminationBracketUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerateEliminationBracketService implements GenerateEliminationBracketUseCase {

    private final TournamentRepositoryPort repository;


    @Override
    public Tournament generate(UUID tournamentId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(
                        "No se encontro un torneo con id " + tournamentId));

        tournament.generateEliminationBracket();

        return repository.save(tournament);
    }
}
