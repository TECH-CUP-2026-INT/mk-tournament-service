package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.service.Enrollment;
import co.edu.escuelaing.techcup.tournament.service.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.service.PaymentOrderStatus;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.PaymentServiceClientPort;
import co.edu.escuelaing.techcup.tournament.service.ports.SyncEnrollmentStatusUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Sincroniza periódicamente el estado de los Enrollment RESERVED con el
 * Payment Service, ya que la consulta síncrona (GetEnrolledTeamsService) solo
 * refleja el estado en vivo mientras alguien está consultando — este job es
 * el que efectivamente transiciona RESERVED -> ENROLLED/REJECTED con el tiempo.
 */
@Service
public class SyncEnrollmentStatusService implements SyncEnrollmentStatusUseCase {

    private final TournamentRepositoryPort tournamentRepository;
    private final PaymentServiceClientPort paymentServiceClient;

    public SyncEnrollmentStatusService(TournamentRepositoryPort tournamentRepository,
                                        PaymentServiceClientPort paymentServiceClient) {
        this.tournamentRepository = tournamentRepository;
        this.paymentServiceClient = paymentServiceClient;
    }

    @Override
    @Scheduled(fixedRate = 300_000)
    public void sync() {
        for (Tournament tournament : tournamentRepository.findAllWithReservedEnrollments()) {
            if (syncReservedEnrollments(tournament)) {
                tournamentRepository.save(tournament);
            }
        }
    }

    private boolean syncReservedEnrollments(Tournament tournament) {
        boolean changed = false;
        for (Enrollment enrollment : tournament.getEnrollments()) {
            if (enrollment.getStatus() != EnrollmentStatus.RESERVED) continue;

            PaymentOrderStatus paymentStatus = paymentServiceClient.getOrderStatus(enrollment.getEnrollmentId());
            if (paymentStatus == PaymentOrderStatus.APPROVED) {
                enrollment.markEnrolled(LocalDateTime.now());
                changed = true;
            } else if (paymentStatus == PaymentOrderStatus.REJECTED) {
                enrollment.markRejected();
                changed = true;
            }
            // PENDING / AWAITING_BANK_CONFIRMATION / UNKNOWN: sin cambios, se reintenta en el próximo ciclo.
        }
        return changed;
    }
}
