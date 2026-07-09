package co.edu.escuelaing.techcup.tournament.application.service;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotDraftException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.port.out.TournamentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteTournamentServiceTest {

    private TournamentRepositoryPort repository;
    private DeleteTournamentService service;

    @BeforeEach
    void setUp() {
        repository = mock(TournamentRepositoryPort.class);
        service = new DeleteTournamentService(repository);
    }

    @Test
    @DisplayName("TC-29: Debe eliminar el torneo cuando el estado es DRAFT")
    void shouldDeleteTournamentWhenStatusIsDraft() {
        String id = "t-001";
        Tournament tournament = new Tournament(id, "TechCup 2026", TournamentStatus.DRAFT);
        when(repository.findById(id)).thenReturn(Optional.of(tournament));

        assertDoesNotThrow(() -> service.delete(id));

        verify(repository).deleteById(id);
    }

    @ParameterizedTest
    @EnumSource(value = TournamentStatus.class, names = {"ACTIVE", "IN_PREPARATION", "IN_PROGRESS", "FINISHED"})
    @DisplayName("TC-29: Debe lanzar TournamentNotDraftException para cualquier estado que no sea DRAFT")
    void shouldThrowExceptionWhenTournamentIsNotDraft(TournamentStatus status) {
        String id = "t-002";
        Tournament tournament = new Tournament(id, "TechCup 2026", status);
        when(repository.findById(id)).thenReturn(Optional.of(tournament));

        TournamentNotDraftException ex = assertThrows(
                TournamentNotDraftException.class,
                () -> service.delete(id)
        );

        assertTrue(ex.getMessage().contains(status.name()));
        verify(repository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("TC-29: Debe lanzar TournamentNotFoundException cuando el torneo no existe")
    void shouldThrowExceptionWhenTournamentNotFound() {
        String id = "t-999";
        when(repository.findById(id)).thenReturn(Optional.empty());

        TournamentNotFoundException ex = assertThrows(
                TournamentNotFoundException.class,
                () -> service.delete(id)
        );

        assertTrue(ex.getMessage().contains(id));
        verify(repository, never()).deleteById(anyString());
    }
}
