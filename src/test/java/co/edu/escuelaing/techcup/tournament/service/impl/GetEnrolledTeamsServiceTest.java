package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Enrollment;
import co.edu.escuelaing.techcup.tournament.service.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.service.PaymentOrderStatus;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.GetEnrolledTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.PaymentServiceClientPort;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
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
        tournament.setTeams(List.of(enrollments));
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
                "t1", "Copa ECI", 5, java.math.BigDecimal.ZERO,
                java.time.LocalDate.now().plusDays(2), java.time.LocalDate.now().plusDays(10),
                java.time.LocalDate.now(), TournamentStatus.ACTIVE,
                List.of(
                        new Enrollment("team1", "Los Tigres", EnrollmentStatus.ENROLLED),
                        new Enrollment("team2", "Los Leones", EnrollmentStatus.RESERVED)
                ),
                List.of()
        );
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
