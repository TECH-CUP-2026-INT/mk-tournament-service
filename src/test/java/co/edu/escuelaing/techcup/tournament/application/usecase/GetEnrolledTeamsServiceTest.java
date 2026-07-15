package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.domain.model.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.PaymentOrderStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetEnrolledTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PaymentServiceClientPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GetEnrolledTeamsServiceTest {

    private TournamentRepositoryPort tournamentRepository;
    private PaymentServiceClientPort paymentServiceClient;
    private GetEnrolledTeamsService service;

    @BeforeEach
    void setUp() {
        tournamentRepository = mock(TournamentRepositoryPort.class);
        paymentServiceClient = mock(PaymentServiceClientPort.class);
        service = new GetEnrolledTeamsService(tournamentRepository, paymentServiceClient);
    }

    private Tournament tournamentWith(Enrollment... enrollments) {
        Tournament tournament = new Tournament("t1", "Copa ECI", TournamentStatus.ACTIVE);
        tournament.setEnrollments(List.of(enrollments));
        when(tournamentRepository.findById("t1")).thenReturn(Optional.of(tournament));
        return tournament;
    }

    @Test
    void getEnrolledTeams_soloEquiposEnrolled_seReportanConDatosLocalesYSinLlamarAlPaymentService() {
        tournamentWith(
                new Enrollment("team1", "Los Tigres", EnrollmentStatus.ENROLLED),
                new Enrollment("team2", "Los Leones", EnrollmentStatus.ENROLLED)
        );

        GetEnrolledTeamsUseCase.EnrolledTeamsView view = service.getEnrolledTeams("t1");

        assertEquals(2, view.enrolled().size());
        assertTrue(view.reserved().isEmpty());
        verifyNoInteractions(paymentServiceClient);
    }

    @Test
    void getEnrolledTeams_equipoReserved_consultaEstadoVivoDePagoEnPaymentService() {
        Enrollment reserved = new Enrollment("team1", "Los Tigres", EnrollmentStatus.RESERVED);
        tournamentWith(reserved);
        when(paymentServiceClient.getOrderStatus(reserved.getEnrollmentId()))
                .thenReturn(PaymentOrderStatus.AWAITING_BANK_CONFIRMATION);

        GetEnrolledTeamsUseCase.EnrolledTeamsView view = service.getEnrolledTeams("t1");

        assertTrue(view.enrolled().isEmpty());
        assertEquals(1, view.reserved().size());
        assertEquals(PaymentOrderStatus.AWAITING_BANK_CONFIRMATION, view.reserved().get(0).livePaymentStatus());
    }

    @Test
    void getEnrolledTeams_mezclaEnrolledYReserved_separaAmbasListasYCuentaTotales() {
        Enrollment enrolled = new Enrollment("team1", "Los Tigres", EnrollmentStatus.ENROLLED);
        Enrollment reserved = new Enrollment("team2", "Los Leones", EnrollmentStatus.RESERVED);
        Enrollment rejected = new Enrollment("team3", "Los Zorros", EnrollmentStatus.REJECTED);
        Enrollment expired = new Enrollment("team4", "Los Osos", EnrollmentStatus.EXPIRED);
        tournamentWith(enrolled, reserved, rejected, expired);
        when(paymentServiceClient.getOrderStatus(reserved.getEnrollmentId()))
                .thenReturn(PaymentOrderStatus.PENDING);

        GetEnrolledTeamsUseCase.EnrolledTeamsView view = service.getEnrolledTeams("t1");

        assertEquals(1, view.enrolled().size());
        assertEquals(1, view.reserved().size());
    }

    @Test
    void getEnrolledTeams_paymentServiceFalla_caeAUnknownSinFallarLaConsulta() {
        Enrollment reserved = new Enrollment("team1", "Los Tigres", EnrollmentStatus.RESERVED);
        tournamentWith(reserved);
        when(paymentServiceClient.getOrderStatus(reserved.getEnrollmentId()))
                .thenReturn(PaymentOrderStatus.UNKNOWN);

        GetEnrolledTeamsUseCase.EnrolledTeamsView view = service.getEnrolledTeams("t1");

        assertEquals(PaymentOrderStatus.UNKNOWN, view.reserved().get(0).livePaymentStatus());
    }

    @Test
    void getEnrolledTeams_calculaAvailableSlotsSobreCapacidadDelTorneo() {
        Tournament tournament = Tournament.reconstruct(
                "t1", "Copa ECI",
                co.edu.escuelaing.techcup.tournament.domain.model.TournamentType.NORMAL,
                co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat.BRACKETS,
                5, java.math.BigDecimal.ZERO,
                java.time.LocalDate.now().plusDays(2), java.time.LocalDate.now().plusDays(10),
                java.time.LocalDate.now(), null, null, TournamentStatus.ACTIVE,
                List.of(),
                List.of(), null, null
        );
        tournament.setEnrollments(List.of(
                new Enrollment("team1", "Los Tigres", EnrollmentStatus.ENROLLED),
                new Enrollment("team2", "Los Leones", EnrollmentStatus.RESERVED)
        ));
        when(tournamentRepository.findById("t1")).thenReturn(Optional.of(tournament));
        when(paymentServiceClient.getOrderStatus(anyString())).thenReturn(PaymentOrderStatus.PENDING);

        GetEnrolledTeamsUseCase.EnrolledTeamsView view = service.getEnrolledTeams("t1");

        assertEquals(3, view.availableSlots());
    }

    @Test
    void getEnrolledTeams_torneoNoExiste_lanzaTournamentNotFoundException() {
        when(tournamentRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> service.getEnrolledTeams("unknown"));
    }
}
