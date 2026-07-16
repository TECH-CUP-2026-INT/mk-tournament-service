package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentCannotBeDeletedException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;
import java.util.UUID;

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
    @DisplayName("TC-51: Debe eliminar el torneo cuando el estado es FINISHED")
    void shouldDeleteTournamentWhenStatusIsFinished() {
        UUID id = UUID.randomUUID();
        Tournament tournament = new Tournament(id, "TechCup 2026", TournamentStatus.FINISHED);
        when(repository.findById(id)).thenReturn(Optional.of(tournament));

        assertDoesNotThrow(() -> service.delete(id));

        verify(repository).deleteById(id);
    }

    @ParameterizedTest
    @EnumSource(value = TournamentStatus.class, names = {"DRAFT", "ACTIVE", "IN_PREPARATION", "IN_PROGRESS"})
    @DisplayName("TC-51: Debe lanzar TournamentCannotBeDeletedException para cualquier estado que no sea FINISHED")
    void shouldThrowExceptionWhenTournamentIsNotFinished(TournamentStatus status) {
        UUID id = UUID.randomUUID();
        Tournament tournament = new Tournament(id, "TechCup 2026", status);
        when(repository.findById(id)).thenReturn(Optional.of(tournament));

        TournamentCannotBeDeletedException ex = assertThrows(
                TournamentCannotBeDeletedException.class,
                () -> service.delete(id)
        );

        assertTrue(ex.getMessage().contains(status.name()));
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("TC-51: Debe lanzar TournamentNotFoundException cuando el torneo no existe")
    void shouldThrowExceptionWhenTournamentNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        TournamentNotFoundException ex = assertThrows(
                TournamentNotFoundException.class,
                () -> service.delete(id)
        );

        assertTrue(ex.getMessage().contains(id.toString()));
        verify(repository, never()).deleteById(any());
    }
}
