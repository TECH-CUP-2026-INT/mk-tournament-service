package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.CheckTeamActiveEnrollmentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckTeamActiveEnrollmentService implements CheckTeamActiveEnrollmentUseCase {

    private final TournamentRepositoryPort tournamentRepository;

    @Override
    public boolean hasActiveEnrollment(UUID teamId) {
        return tournamentRepository.existsActiveEnrollmentForTeam(teamId);
    }
}
