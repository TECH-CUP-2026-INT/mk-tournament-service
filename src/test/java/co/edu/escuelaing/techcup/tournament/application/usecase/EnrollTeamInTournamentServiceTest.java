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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    private Tournament tournamentWith(UUID tournamentId, TournamentStatus status, int numberOfTeams, List<Enrollment> enrollments) {
        return Tournament.reconstruct(
                tournamentId, "Copa ECI", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                numberOfTeams, BigDecimal.valueOf(50000),
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(20), LocalDate.now().plusDays(1),
                null, null, status,
                List.of(), List.of(), null, null, false, true, enrollments, null
        );
    }

    @Test
    void enrollTeam_flujoFeliz_reservaCupoYCreaOrdenDePago() {
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        Tournament tournament = tournamentWith(tournamentId, TournamentStatus.ACTIVE, 8, List.of());
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(teamServiceClient.getTeamInfo(teamId)).thenReturn(new TeamServiceClientPort.TeamInfo("Los Tigres", 9));
        when(paymentServiceClient.createOrder(any(UUID.class), any(UUID.class), any(UUID.class), any()))
                .thenReturn(new PaymentServiceClientPort.PaymentOrderReference("po-1", null));

        Enrollment result = service.enrollTeam(tournamentId, teamId);

        assertEquals(EnrollmentStatus.RESERVED, result.getStatus());
        assertEquals(teamId, result.getTeamId());
        assertEquals("Los Tigres", result.getTeamName());
        assertNotNull(result.getReservationExpiresAt());
        verify(paymentServiceClient).createOrder(result.getEnrollmentId(), teamId, tournamentId, tournament.getCost());
        verify(tournamentRepository).save(tournament);
    }

    @Test
    void enrollTeam_rosterFueraDeRango_lanzaTeamRosterSizeInvalidExceptionSinGuardar() {
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        Tournament tournament = tournamentWith(tournamentId, TournamentStatus.ACTIVE, 8, List.of());
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(teamServiceClient.getTeamInfo(teamId)).thenReturn(new TeamServiceClientPort.TeamInfo("Los Tigres", 5));

        assertThrows(TeamRosterSizeInvalidException.class, () -> service.enrollTeam(tournamentId, teamId));

        verify(tournamentRepository, never()).save(any());
        verifyNoInteractions(paymentServiceClient);
    }

    @Test
    void enrollTeam_torneoNoActivo_lanzaTournamentNotActiveForEnrollmentException() {
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        Tournament tournament = tournamentWith(tournamentId, TournamentStatus.DRAFT, 8, List.of());
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(teamServiceClient.getTeamInfo(teamId)).thenReturn(new TeamServiceClientPort.TeamInfo("Los Tigres", 9));

        assertThrows(TournamentNotActiveForEnrollmentException.class, () -> service.enrollTeam(tournamentId, teamId));

        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void enrollTeam_sinCupoDisponible_lanzaNoAvailableSlotsException() {
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        Enrollment existing = new Enrollment(UUID.randomUUID(), "Los Leones", EnrollmentStatus.ENROLLED);
        Tournament tournament = tournamentWith(tournamentId, TournamentStatus.ACTIVE, 1, List.of(existing));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(teamServiceClient.getTeamInfo(teamId)).thenReturn(new TeamServiceClientPort.TeamInfo("Los Tigres", 9));

        assertThrows(NoAvailableSlotsException.class, () -> service.enrollTeam(tournamentId, teamId));

        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void enrollTeam_fallaCreacionDeOrdenDePago_revierteLaReservaYPropagaLaExcepcion() {
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        Tournament tournament = tournamentWith(tournamentId, TournamentStatus.ACTIVE, 8, List.of());
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(teamServiceClient.getTeamInfo(teamId)).thenReturn(new TeamServiceClientPort.TeamInfo("Los Tigres", 9));
        when(paymentServiceClient.createOrder(any(UUID.class), any(UUID.class), any(UUID.class), any()))
                .thenThrow(new PaymentOrderCreationFailedException(UUID.randomUUID(), new RuntimeException("timeout")));

        assertThrows(PaymentOrderCreationFailedException.class, () -> service.enrollTeam(tournamentId, teamId));

        assertTrue(tournament.getEnrollments().isEmpty(), "El enrollment reservado debe revertirse");
        verify(tournamentRepository, times(2)).save(tournament);
    }

    @Test
    void enrollTeam_torneoNoExiste_lanzaTournamentNotFoundException() {
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());
        when(teamServiceClient.getTeamInfo(teamId)).thenReturn(new TeamServiceClientPort.TeamInfo("Los Tigres", 9));

        assertThrows(TournamentNotFoundException.class, () -> service.enrollTeam(tournamentId, teamId));
    }
}
