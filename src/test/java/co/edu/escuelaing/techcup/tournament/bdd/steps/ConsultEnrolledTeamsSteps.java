package co.edu.escuelaing.techcup.tournament.bdd.steps;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Enrollment;
import co.edu.escuelaing.techcup.tournament.service.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.service.PaymentOrderStatus;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.impl.GetEnrolledTeamsService;
import co.edu.escuelaing.techcup.tournament.service.ports.GetEnrolledTeamsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.PaymentServiceClientPort;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConsultEnrolledTeamsSteps {

    private TournamentRepositoryPort repository;
    private PaymentServiceClientPort paymentServiceClient;
    private GetEnrolledTeamsService service;

    private final Map<String, Tournament> tournaments = new HashMap<>();
    private final Map<String, String> lastReservationEnrollmentIdByTournament = new HashMap<>();

    private GetEnrolledTeamsUseCase.EnrolledTeamsView result;
    private Exception thrownException;

    @Before
    public void setUp() {
        repository = mock(TournamentRepositoryPort.class);
        paymentServiceClient = mock(PaymentServiceClientPort.class);
        service = new GetEnrolledTeamsService(repository, paymentServiceClient);
        tournaments.clear();
        lastReservationEnrollmentIdByTournament.clear();
        result = null;
        thrownException = null;
    }

    @Given("a tournament exists with id {string} and capacity for {int} teams")
    public void aTournamentExistsWithCapacity(String id, int capacity) {
        Tournament tournament = Tournament.reconstruct(
                id, "Test Tournament", capacity, java.math.BigDecimal.ZERO,
                java.time.LocalDate.now().plusDays(2), java.time.LocalDate.now().plusDays(10),
                java.time.LocalDate.now(), TournamentStatus.ACTIVE,
                new ArrayList<>(), new ArrayList<>()
        );
        tournaments.put(id, tournament);
        when(repository.findById(id)).thenReturn(Optional.of(tournament));
    }

    @Given("no tournament for enrollment queries exists with id {string}")
    public void noTournamentForEnrollmentQueriesExistsWithId(String id) {
        when(repository.findById(id)).thenReturn(Optional.empty());
    }

    @Given("the tournament has an enrolled team {string} named {string}")
    public void theTournamentHasAnEnrolledTeam(String teamId, String teamName) {
        addTeamToLastTournament(new Enrollment(teamId, teamName, EnrollmentStatus.ENROLLED));
    }

    @Given("the tournament has a reserved team {string} named {string}")
    public void theTournamentHasAReservedTeam(String teamId, String teamName) {
        Enrollment enrollment = new Enrollment(teamId, teamName, EnrollmentStatus.RESERVED);
        String tournamentId = addTeamToLastTournament(enrollment);
        lastReservationEnrollmentIdByTournament.put(tournamentId, enrollment.getEnrollmentId());
    }

    @Given("the payment service reports status {string} for that reservation")
    public void thePaymentServiceReportsStatus(String status) {
        String enrollmentId = lastReservationEnrollmentIdByTournament.values().stream()
                .reduce((first, second) -> second).orElseThrow();
        when(paymentServiceClient.getOrderStatus(enrollmentId)).thenReturn(PaymentOrderStatus.valueOf(status));
    }

    @Given("the payment service is unreachable for that reservation")
    public void thePaymentServiceIsUnreachableForThatReservation() {
        String enrollmentId = lastReservationEnrollmentIdByTournament.values().stream()
                .reduce((first, second) -> second).orElseThrow();
        when(paymentServiceClient.getOrderStatus(enrollmentId)).thenReturn(PaymentOrderStatus.UNKNOWN);
    }

    @When("a user requests the enrolled teams of tournament {string}")
    public void aUserRequestsTheEnrolledTeamsOfTournament(String tournamentId) {
        try {
            result = service.getEnrolledTeams(tournamentId);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the response should list {int} enrolled teams and {int} reserved teams")
    public void theResponseShouldListEnrolledAndReservedTeams(int enrolledCount, int reservedCount) {
        assertNull(thrownException, "No exception should have been thrown");
        assertEquals(enrolledCount, result.enrolled().size());
        assertEquals(reservedCount, result.reserved().size());
    }

    @Then("the payment service should not have been called")
    public void thePaymentServiceShouldNotHaveBeenCalled() {
        verifyNoInteractions(paymentServiceClient);
    }

    @Then("the reserved team should show live payment status {string}")
    public void theReservedTeamShouldShowLivePaymentStatus(String status) {
        assertEquals(PaymentOrderStatus.valueOf(status), result.reserved().get(0).livePaymentStatus());
    }

    @Then("the consultation should fail with a not found error")
    public void theConsultationShouldFailWithANotFoundError() {
        assertNotNull(thrownException, "An exception should have been thrown");
        assertInstanceOf(TournamentNotFoundException.class, thrownException);
    }

    private String addTeamToLastTournament(Enrollment enrollment) {
        String tournamentId = tournaments.keySet().stream()
                .reduce((first, second) -> second).orElseThrow();
        Tournament tournament = tournaments.get(tournamentId);
        List<Enrollment> teams = new ArrayList<>(tournament.getTeams());
        teams.add(enrollment);
        tournament.setTeams(teams);
        return tournamentId;
    }
}
