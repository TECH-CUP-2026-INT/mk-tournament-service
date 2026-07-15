package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.NoAvailableSlotsException;
import co.edu.escuelaing.techcup.tournament.domain.exception.PaymentOrderCreationFailedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamRosterSizeInvalidException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotActiveForEnrollmentException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.domain.model.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PaymentServiceClientPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TeamServiceClientPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EnrollTeamInTournamentServiceTest {

    private TournamentRepositoryPort tournamentRepository;
    private TeamServiceClientPort teamServiceClient;
    private PaymentServiceClientPort paymentServiceClient;
    private EnrollTeamInTournamentService service;

    @BeforeEach
    void setUp() {
        tournamentRepository = mock(TournamentRepositoryPort.class);
        teamServiceClient = mock(TeamServiceClientPort.class);
        paymentServiceClient = mock(PaymentServiceClientPort.class);
        service = new EnrollTeamInTournamentService(tournamentRepository, teamServiceClient, paymentServiceClient);
        when(tournamentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Tournament tournamentWith(TournamentStatus status, int numberOfTeams, List<Enrollment> enrollments) {
        return Tournament.reconstruct(
                "t1", "Copa ECI", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                numberOfTeams, BigDecimal.valueOf(50000),
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(20), LocalDate.now().plusDays(1),
                null, null, status,
                List.of(), List.of(), null, null, false, true, enrollments, null
        );
    }

    @Test
    void enrollTeam_flujoFeliz_reservaCupoYCreaOrdenDePago() {
        Tournament tournament = tournamentWith(TournamentStatus.ACTIVE, 8, List.of());
        when(tournamentRepository.findById("t1")).thenReturn(Optional.of(tournament));
        when(teamServiceClient.getTeamInfo("team1")).thenReturn(new TeamServiceClientPort.TeamInfo("Los Tigres", 9));
        when(paymentServiceClient.createOrder(anyString(), anyString(), anyString(), any()))
                .thenReturn(new PaymentServiceClientPort.PaymentOrderReference("po-1", null));

        Enrollment result = service.enrollTeam("t1", "team1");

        assertEquals(EnrollmentStatus.RESERVED, result.getStatus());
        assertEquals("team1", result.getTeamId());
        assertEquals("Los Tigres", result.getTeamName());
        assertNotNull(result.getReservationExpiresAt());
        verify(paymentServiceClient).createOrder(result.getEnrollmentId(), "team1", "t1", tournament.getCost());
        verify(tournamentRepository).save(tournament);
    }

    @Test
    void enrollTeam_rosterFueraDeRango_lanzaTeamRosterSizeInvalidExceptionSinGuardar() {
        Tournament tournament = tournamentWith(TournamentStatus.ACTIVE, 8, List.of());
        when(tournamentRepository.findById("t1")).thenReturn(Optional.of(tournament));
        when(teamServiceClient.getTeamInfo("team1")).thenReturn(new TeamServiceClientPort.TeamInfo("Los Tigres", 5));

        assertThrows(TeamRosterSizeInvalidException.class, () -> service.enrollTeam("t1", "team1"));

        verify(tournamentRepository, never()).save(any());
        verifyNoInteractions(paymentServiceClient);
    }

    @Test
    void enrollTeam_torneoNoActivo_lanzaTournamentNotActiveForEnrollmentException() {
        Tournament tournament = tournamentWith(TournamentStatus.DRAFT, 8, List.of());
        when(tournamentRepository.findById("t1")).thenReturn(Optional.of(tournament));
        when(teamServiceClient.getTeamInfo("team1")).thenReturn(new TeamServiceClientPort.TeamInfo("Los Tigres", 9));

        assertThrows(TournamentNotActiveForEnrollmentException.class, () -> service.enrollTeam("t1", "team1"));

        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void enrollTeam_sinCupoDisponible_lanzaNoAvailableSlotsException() {
        Enrollment existing = new Enrollment("team0", "Los Leones", EnrollmentStatus.ENROLLED);
        Tournament tournament = tournamentWith(TournamentStatus.ACTIVE, 1, List.of(existing));
        when(tournamentRepository.findById("t1")).thenReturn(Optional.of(tournament));
        when(teamServiceClient.getTeamInfo("team1")).thenReturn(new TeamServiceClientPort.TeamInfo("Los Tigres", 9));

        assertThrows(NoAvailableSlotsException.class, () -> service.enrollTeam("t1", "team1"));

        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void enrollTeam_fallaCreacionDeOrdenDePago_revierteLaReservaYPropagaLaExcepcion() {
        Tournament tournament = tournamentWith(TournamentStatus.ACTIVE, 8, List.of());
        when(tournamentRepository.findById("t1")).thenReturn(Optional.of(tournament));
        when(teamServiceClient.getTeamInfo("team1")).thenReturn(new TeamServiceClientPort.TeamInfo("Los Tigres", 9));
        when(paymentServiceClient.createOrder(anyString(), anyString(), anyString(), any()))
                .thenThrow(new PaymentOrderCreationFailedException("enr-1", new RuntimeException("timeout")));

        assertThrows(PaymentOrderCreationFailedException.class, () -> service.enrollTeam("t1", "team1"));

        assertTrue(tournament.getEnrollments().isEmpty(), "El enrollment reservado debe revertirse");
        verify(tournamentRepository, times(2)).save(tournament);
    }

    @Test
    void enrollTeam_torneoNoExiste_lanzaTournamentNotFoundException() {
        when(tournamentRepository.findById("unknown")).thenReturn(Optional.empty());
        when(teamServiceClient.getTeamInfo("team1")).thenReturn(new TeamServiceClientPort.TeamInfo("Los Tigres", 9));

        assertThrows(TournamentNotFoundException.class, () -> service.enrollTeam("unknown", "team1"));
    }
}
