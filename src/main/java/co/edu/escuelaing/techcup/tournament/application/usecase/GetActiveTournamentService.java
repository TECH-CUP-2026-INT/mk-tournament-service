package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetActiveTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetActiveTournamentService implements GetActiveTournamentUseCase {

    private final TournamentRepositoryPort tournamentRepository;

    @Override
    public Optional<Tournament> getActive() {
        List<Tournament> activeTournaments = tournamentRepository.findAllByStatus(TournamentStatus.ACTIVE);
        if (activeTournaments.isEmpty()) {
            List<Tournament> inProgress = tournamentRepository.findAllByStatus(TournamentStatus.IN_PROGRESS);
            return inProgress.isEmpty() ? Optional.empty() : Optional.of(inProgress.getFirst());
        }
        return Optional.of(activeTournaments.getFirst());
    }
}
