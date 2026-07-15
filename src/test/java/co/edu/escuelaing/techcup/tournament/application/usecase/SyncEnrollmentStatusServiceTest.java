package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.domain.model.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.PaymentOrderStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PaymentServiceClientPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class SyncEnrollmentStatusServiceTest {

    private TournamentRepositoryPort tournamentRepository;
    private PaymentServiceClientPort paymentServiceClient;
    private SyncEnrollmentStatusService service;

    @BeforeEach
    void setUp() {
        tournamentRepository = mock(TournamentRepositoryPort.class);
        paymentServiceClient = mock(PaymentServiceClientPort.class);
        service = new SyncEnrollmentStatusService(tournamentRepository, paymentServiceClient);
    }

    private Tournament tournamentWith(Enrollment enrollment) {
        return Tournament.reconstruct(
                "t1", "Copa ECI", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                8, BigDecimal.valueOf(50000),
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(20), LocalDate.now().plusDays(1),
                null, null, TournamentStatus.ACTIVE,
                List.of(), List.of(), null, null, false, true, List.of(enrollment), null
        );
    }

    @Test
    void sync_paymentServiceApproved_pasaAEnrolledYFijaConfirmationDate() {
        Enrollment reserved = new Enrollment("team1", "Los Tigres", EnrollmentStatus.RESERVED);
        Tournament tournament = tournamentWith(reserved);
        when(tournamentRepository.findAllWithReservedEnrollments()).thenReturn(List.of(tournament));
        when(paymentServiceClient.getOrderStatus(reserved.getEnrollmentId())).thenReturn(PaymentOrderStatus.APPROVED);

        service.sync();

        assertEquals(EnrollmentStatus.ENROLLED, reserved.getStatus());
        assertNotNull(reserved.getConfirmationDate());
        verify(tournamentRepository).save(tournament);
    }

    @Test
    void sync_paymentServiceRejected_pasaARejected() {
        Enrollment reserved = new Enrollment("team1", "Los Tigres", EnrollmentStatus.RESERVED);
        Tournament tournament = tournamentWith(reserved);
        when(tournamentRepository.findAllWithReservedEnrollments()).thenReturn(List.of(tournament));
        when(paymentServiceClient.getOrderStatus(reserved.getEnrollmentId())).thenReturn(PaymentOrderStatus.REJECTED);

        service.sync();

        assertEquals(EnrollmentStatus.REJECTED, reserved.getStatus());
        verify(tournamentRepository).save(tournament);
    }

    @Test
    void sync_paymentServicePendiente_noCambiaNiGuarda() {
        Enrollment reserved = new Enrollment("team1", "Los Tigres", EnrollmentStatus.RESERVED);
        Tournament tournament = tournamentWith(reserved);
        when(tournamentRepository.findAllWithReservedEnrollments()).thenReturn(List.of(tournament));
        when(paymentServiceClient.getOrderStatus(reserved.getEnrollmentId())).thenReturn(PaymentOrderStatus.PENDING);

        service.sync();

        assertEquals(EnrollmentStatus.RESERVED, reserved.getStatus());
        assertNull(reserved.getConfirmationDate());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void sync_paymentServiceUnknown_noCambiaNiGuarda() {
        Enrollment reserved = new Enrollment("team1", "Los Tigres", EnrollmentStatus.RESERVED);
        Tournament tournament = tournamentWith(reserved);
        when(tournamentRepository.findAllWithReservedEnrollments()).thenReturn(List.of(tournament));
        when(paymentServiceClient.getOrderStatus(reserved.getEnrollmentId())).thenReturn(PaymentOrderStatus.UNKNOWN);

        service.sync();

        assertEquals(EnrollmentStatus.RESERVED, reserved.getStatus());
        verify(tournamentRepository, never()).save(any());
    }
}
