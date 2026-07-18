package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.domain.model.PaymentOrderStatus;

import java.util.List;
import java.util.UUID;

public interface GetEnrolledTeamsUseCase {

    EnrolledTeamsView getEnrolledTeams(UUID tournamentId);

    record EnrolledTeamsView(List<Enrollment> enrolled, List<ReservedTeamView> reserved, int availableSlots) {}

    record ReservedTeamView(Enrollment enrollment, PaymentOrderStatus livePaymentStatus) {}
}
