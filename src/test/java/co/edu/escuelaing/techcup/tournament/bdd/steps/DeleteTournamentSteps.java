package co.edu.escuelaing.techcup.tournament.bdd.steps;

import co.edu.escuelaing.techcup.tournament.service.impl.DeleteTournamentService;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentCannotBeDeletedException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeleteTournamentSteps {

    private TournamentRepositoryPort repository;
    private DeleteTournamentService service;

    private Exception thrownException;

    @Before
    public void setUp() {
        repository = mock(TournamentRepositoryPort.class);
        service = new DeleteTournamentService(repository);
        thrownException = null;
    }

    @Given("a tournament exists with id {string} and status {string}")
    public void aTournamentExistsWithIdAndStatus(String id, String status) {
        Tournament tournament = new Tournament(id, "Test Tournament", TournamentStatus.valueOf(status));
        when(repository.findById(id)).thenReturn(Optional.of(tournament));
    }

    @Given("no tournament exists with id {string}")
    public void noTournamentExistsWithId(String id) {
        when(repository.findById(id)).thenReturn(Optional.empty());
    }

    @When("the organizer requests to delete tournament with id {string}")
    public void theOrganizerRequestsToDeleteTournamentWithId(String id) {
        try {
            service.delete(id);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the tournament should be deleted successfully")
    public void theTournamentShouldBeDeletedSuccessfully() {
        assertNull(thrownException, "No exception should have been thrown");
        verify(repository, times(1)).deleteById(anyString());
    }

    @Then("the deletion should be rejected with a business rule violation")
    public void theDeletionShouldBeRejectedWithABusinessRuleViolation() {
        assertNotNull(thrownException, "An exception should have been thrown");
        assertInstanceOf(TournamentCannotBeDeletedException.class, thrownException);
        verify(repository, never()).deleteById(anyString());
    }

    @Then("the deletion should fail with a not found error")
    public void theDeletionShouldFailWithANotFoundError() {
        assertNotNull(thrownException, "An exception should have been thrown");
        assertInstanceOf(TournamentNotFoundException.class, thrownException);
        verify(repository, never()).deleteById(anyString());
    }
}
