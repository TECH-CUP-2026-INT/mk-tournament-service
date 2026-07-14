package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.ports.CourtRepositoryPort;
import co.edu.escuelaing.techcup.tournament.service.ports.ViewMatchCourtUseCase;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ViewMatchCourtService implements ViewMatchCourtUseCase {

    private final CourtRepositoryPort courtRepository;

    public ViewMatchCourtService(CourtRepositoryPort courtRepository) {
        this.courtRepository = courtRepository;
    }

    @Override
    public Optional<Court> getCourtByMatch(String matchId) {
        return courtRepository.findByMatchId(matchId);
    }
}
