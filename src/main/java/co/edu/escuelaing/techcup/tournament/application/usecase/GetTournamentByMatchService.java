package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetTournamentByMatchUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Punto de integración para otros servicios (ej. Competencia) que solo tienen
 * un matchId y necesitan el tournamentId para correlacionar eventos.
 */
@Service
@RequiredArgsConstructor
public class GetTournamentByMatchService implements GetTournamentByMatchUseCase {

    private final TournamentRepositoryPort tournamentRepository;

    @Override
    public Tournament getByMatch(UUID matchId) {
        return tournamentRepository.findByMatchId(matchId)
                .orElseThrow(() -> new MatchupNotFoundException(matchId));
    }
}
