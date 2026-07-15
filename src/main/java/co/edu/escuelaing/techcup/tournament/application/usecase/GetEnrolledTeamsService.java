package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.domain.model.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetEnrolledTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PaymentServiceClientPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEnrolledTeamsService implements GetEnrolledTeamsUseCase {

    private final TournamentRepositoryPort tournamentRepository;
    private final PaymentServiceClientPort paymentServiceClient;


    @Override
    public EnrolledTeamsView getEnrolledTeams(String tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));

        List<Enrollment> enrolled = tournament.getEnrollments().stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                .toList();

        List<ReservedTeamView> reserved = tournament.getEnrollments().stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.RESERVED)
                .map(e -> new ReservedTeamView(e, paymentServiceClient.getOrderStatus(e.getEnrollmentId())))
                .toList();

        int availableSlots = tournament.getNumberOfTeams() - (enrolled.size() + reserved.size());

        return new EnrolledTeamsView(enrolled, reserved, availableSlots);
    }
}
