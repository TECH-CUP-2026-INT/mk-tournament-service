package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.PaymentOrderCreationFailedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.EnrollTeamInTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PaymentServiceClientPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TeamServiceClientPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollTeamInTournamentService implements EnrollTeamInTournamentUseCase {

    private static final int MAX_SAVE_ATTEMPTS = 3;

    private final TournamentRepositoryPort tournamentRepository;
    private final TeamServiceClientPort teamServiceClient;
    private final PaymentServiceClientPort paymentServiceClient;


    @Override
    public Enrollment enrollTeam(UUID tournamentId, UUID teamId) {
        TeamServiceClientPort.TeamInfo teamInfo = teamServiceClient.getTeamInfo(teamId);

        ReservationResult reservation = reserveSlot(tournamentId, teamId, teamInfo);

        try {
            paymentServiceClient.createOrder(
                    reservation.enrollment().getEnrollmentId(), teamId, tournamentId, reservation.cost());
        } catch (PaymentOrderCreationFailedException ex) {
            revertReservation(tournamentId, reservation.enrollment().getEnrollmentId());
            throw ex;
        }

        return reservation.enrollment();
    }

    private ReservationResult reserveSlot(UUID tournamentId, UUID teamId, TeamServiceClientPort.TeamInfo teamInfo) {
        OptimisticLockingFailureException lastFailure = null;
        for (int attempt = 1; attempt <= MAX_SAVE_ATTEMPTS; attempt++) {
            Tournament tournament = tournamentRepository.findById(tournamentId)
                    .orElseThrow(() -> new TournamentNotFoundException(tournamentId.toString()));

            Enrollment enrollment = tournament.enrollTeam(teamId, teamInfo.teamName(), teamInfo.rosterSize());

            try {
                tournamentRepository.save(tournament);
                return new ReservationResult(enrollment, tournament.getCost());
            } catch (OptimisticLockingFailureException ex) {
                lastFailure = ex;
            }
        }
        throw lastFailure;
    }

    private record ReservationResult(Enrollment enrollment, java.math.BigDecimal cost) {}

    private void revertReservation(UUID tournamentId, UUID enrollmentId) {
        tournamentRepository.findById(tournamentId).ifPresent(tournament -> {
            tournament.getEnrollments().removeIf(e -> e.getEnrollmentId().equals(enrollmentId));
            tournamentRepository.save(tournament);
        });
    }
}
