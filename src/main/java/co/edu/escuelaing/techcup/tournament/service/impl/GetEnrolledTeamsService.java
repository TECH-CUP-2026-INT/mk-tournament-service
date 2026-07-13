package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Enrollment;
import co.edu.escuelaing.techcup.tournament.service.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.GetEnrolledTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.PaymentServiceClientPort;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetEnrolledTeamsService implements GetEnrolledTeamsUseCase {

    private final TournamentRepositoryPort tournamentRepository;
    private final PaymentServiceClientPort paymentServiceClient;

    public GetEnrolledTeamsService(TournamentRepositoryPort tournamentRepository,
                                    PaymentServiceClientPort paymentServiceClient) {
        this.tournamentRepository = tournamentRepository;
        this.paymentServiceClient = paymentServiceClient;
    }

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
