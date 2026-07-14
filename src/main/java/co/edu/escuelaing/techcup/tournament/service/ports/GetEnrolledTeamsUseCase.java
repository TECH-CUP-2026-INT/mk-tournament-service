package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Enrollment;
import co.edu.escuelaing.techcup.tournament.service.PaymentOrderStatus;

import java.util.List;

public interface GetEnrolledTeamsUseCase {

    EnrolledTeamsView getEnrolledTeams(String tournamentId);

    record EnrolledTeamsView(List<Enrollment> enrolled, List<ReservedTeamView> reserved, int availableSlots) {}

    record ReservedTeamView(Enrollment enrollment, PaymentOrderStatus livePaymentStatus) {}
}
