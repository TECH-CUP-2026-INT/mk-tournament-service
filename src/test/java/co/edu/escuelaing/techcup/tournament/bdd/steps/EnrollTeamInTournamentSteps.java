package co.edu.escuelaing.techcup.tournament.bdd.steps;

import co.edu.escuelaing.techcup.tournament.exception.NoAvailableSlotsException;
import co.edu.escuelaing.techcup.tournament.exception.PaymentOrderCreationFailedException;
import co.edu.escuelaing.techcup.tournament.exception.TeamRosterSizeInvalidException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotActiveForEnrollmentException;
import co.edu.escuelaing.techcup.tournament.service.Enrollment;
import co.edu.escuelaing.techcup.tournament.service.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.impl.EnrollTeamInTournamentService;
import co.edu.escuelaing.techcup.tournament.service.ports.PaymentServiceClientPort;
import co.edu.escuelaing.techcup.tournament.service.ports.TeamServiceClientPort;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class EnrollTeamInTournamentSteps {

    private TournamentRepositoryPort repository;
    private TeamServiceClientPort teamServiceClient;
    private PaymentServiceClientPort paymentServiceClient;
    private EnrollTeamInTournamentService service;

    private final Map<String, Tournament> tournaments = new HashMap<>();

    private Enrollment result;
    private Exception thrownException;

    @Before
    public void setUp() {
        repository = mock(TournamentRepositoryPort.class);
        teamServiceClient = mock(TeamServiceClientPort.class);
        paymentServiceClient = mock(PaymentServiceClientPort.class);
        service = new EnrollTeamInTournamentService(repository, teamServiceClient, paymentServiceClient);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        tournaments.clear();
        result = null;
        thrownException = null;
    }

    @Given("an enrollment tournament {string} exists in status {string} with {int} slots and {int} reservations")
    public void anEnrollmentTournamentExists(String id, String status, int slots, int reservations) {
        List<Enrollment> seeded = new ArrayList<>();
        for (int i = 0; i < reservations; i++) {
            seeded.add(new Enrollment("seed-team-" + i, "Equipo Semilla " + i, EnrollmentStatus.RESERVED));
        }
        Tournament tournament = Tournament.reconstruct(
                id, "Torneo de Prueba", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                slots, BigDecimal.valueOf(50000),
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(20), LocalDate.now().plusDays(1),
                null, null, TournamentStatus.valueOf(status),
                List.of(), List.of(), null, null, false, true, seeded, null
        );
        tournaments.put(id, tournament);
        when(repository.findById(id)).thenReturn(Optional.of(tournament));
    }

    @Given("the team {string} has {int} registered players named {string}")
    public void theTeamHasRegisteredPlayers(String teamId, int playerCount, String teamName) {
        when(teamServiceClient.getTeamInfo(teamId)).thenReturn(
                new TeamServiceClientPort.TeamInfo(teamName, playerCount));
    }

    @Given("the payment service accepts the order creation")
    public void thePaymentServiceAcceptsTheOrderCreation() {
        when(paymentServiceClient.createOrder(anyString(), anyString(), anyString(), any()))
                .thenReturn(new PaymentServiceClientPort.PaymentOrderReference("po-1", null));
    }

    @Given("the payment service fails to create the order")
    public void thePaymentServiceFailsToCreateTheOrder() {
        when(paymentServiceClient.createOrder(anyString(), anyString(), anyString(), any()))
                .thenThrow(new PaymentOrderCreationFailedException("enr-x", new RuntimeException("timeout")));
    }

    @When("the captain enrolls team {string} in tournament {string}")
    public void theCaptainEnrollsTeamInTournament(String teamId, String tournamentId) {
        try {
            result = service.enrollTeam(tournamentId, teamId);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the enrollment should be created in status {string}")
    public void theEnrollmentShouldBeCreatedInStatus(String status) {
        assertNull(thrownException, "No exception should have been thrown");
        assertNotNull(result);
        assertEquals(EnrollmentStatus.valueOf(status), result.getStatus());
    }

    @Then("the enrollment should be rejected with a roster size error")
    public void theEnrollmentShouldBeRejectedWithARosterSizeError() {
        assertInstanceOf(TeamRosterSizeInvalidException.class, thrownException);
    }

    @Then("the enrollment should be rejected because the tournament is not active")
    public void theEnrollmentShouldBeRejectedBecauseTheTournamentIsNotActive() {
        assertInstanceOf(TournamentNotActiveForEnrollmentException.class, thrownException);
    }

    @Then("the enrollment should be rejected because there are no available slots")
    public void theEnrollmentShouldBeRejectedBecauseThereAreNoAvailableSlots() {
        assertInstanceOf(NoAvailableSlotsException.class, thrownException);
    }

    @Then("the enrollment should be rejected because the payment order could not be created")
    public void theEnrollmentShouldBeRejectedBecauseThePaymentOrderCouldNotBeCreated() {
        assertInstanceOf(PaymentOrderCreationFailedException.class, thrownException);
    }

    @Then("no reservation should remain for team {string} in tournament {string}")
    public void noReservationShouldRemainForTeamInTournament(String teamId, String tournamentId) {
        Tournament tournament = tournaments.get(tournamentId);
        assertTrue(tournament.getEnrollments().stream().noneMatch(e -> e.getTeamId().equals(teamId)));
    }
}
