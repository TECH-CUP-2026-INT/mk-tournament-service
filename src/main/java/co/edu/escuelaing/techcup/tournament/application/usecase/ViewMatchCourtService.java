package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.CourtRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewMatchCourtUseCase;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ViewMatchCourtService implements ViewMatchCourtUseCase {

    private final CourtRepositoryPort courtRepository;


    @Override
    public Optional<Court> getCourtByMatch(String matchId) {
        return courtRepository.findByMatchId(matchId);
    }
}
