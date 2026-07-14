package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.service.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetChampionServiceTest {

    @Test
    void getChampion_whenAssigned_retornaElCampeonPublicado() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Tournament tournament = Tournament.reconstruct(
                "t1", "TechCup", 4, BigDecimal.ZERO,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(10), LocalDate.now(),
                TournamentStatus.IN_PROGRESS, new ArrayList<>(), new ArrayList<>(),
                "home", ChampionResolution.REGULATION_TIME
        );

        when(repository.findById("t1")).thenReturn(Optional.of(tournament));

        GetChampionService service = new GetChampionService(repository);
        ChampionAssignment result = service.getChampion("t1");

        assertEquals("home", result.championTeamId());
        assertEquals(ChampionResolution.REGULATION_TIME, result.resolution());
    }

    @Test
    void getChampion_whenNotYetAssigned_lanzaChampionAssignmentNotAllowedException() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Tournament tournament = Tournament.reconstruct(
                "t1", "TechCup", 4, BigDecimal.ZERO,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(10), LocalDate.now(),
                TournamentStatus.IN_PROGRESS, new ArrayList<>(), new ArrayList<>()
        );

        when(repository.findById("t1")).thenReturn(Optional.of(tournament));

        GetChampionService service = new GetChampionService(repository);

        assertThrows(ChampionAssignmentNotAllowedException.class, () -> service.getChampion("t1"));
    }

    @Test
    void getChampion_whenTournamentNotFound_lanzaTournamentNotFoundException() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findById("missing")).thenReturn(Optional.empty());

        GetChampionService service = new GetChampionService(repository);

        assertThrows(TournamentNotFoundException.class, () -> service.getChampion("missing"));
    }
}
