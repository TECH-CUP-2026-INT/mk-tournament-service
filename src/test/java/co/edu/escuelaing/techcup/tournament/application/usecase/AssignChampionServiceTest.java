package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionPendingPenaltiesException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssignChampionServiceTest {

    @Test
    void assignChampion_whenNoTie_persistsChampionByRegulationTime() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Match finalMatch = new Match("final-1", "home", "away", MatchStatus.FINISHED,
                true, 3, 1, null);
        Tournament tournament = Tournament.reconstruct(
                "t1", "TechCup", 4, BigDecimal.ZERO,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(10), LocalDate.now(),
                TournamentStatus.IN_PROGRESS, new ArrayList<>(), new ArrayList<>(List.of(finalMatch))
        );

        when(repository.findById("t1")).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        AssignChampionService service = new AssignChampionService(repository);
        ChampionAssignment result = service.assignChampion("t1", "final-1");

        assertEquals("home", result.championTeamId());
        assertEquals(ChampionResolution.REGULATION_TIME, result.resolution());
        verify(repository).save(tournament);
    }

    @Test
    void assignChampion_whenTieWithPenalties_persistsChampionByPenalties() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Match finalMatch = new Match("final-1", "home", "away", MatchStatus.FINISHED,
                true, 2, 2, "away");
        Tournament tournament = Tournament.reconstruct(
                "t1", "TechCup", 4, BigDecimal.ZERO,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(10), LocalDate.now(),
                TournamentStatus.IN_PROGRESS, new ArrayList<>(), new ArrayList<>(List.of(finalMatch))
        );

        when(repository.findById("t1")).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        AssignChampionService service = new AssignChampionService(repository);
        ChampionAssignment result = service.assignChampion("t1", "final-1");

        assertEquals("away", result.championTeamId());
        assertEquals(ChampionResolution.PENALTIES, result.resolution());
    }

    @Test
    void assignChampion_whenTieWithoutPenalties_throwsPendingPenalties() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Match finalMatch = new Match("final-1", "home", "away", MatchStatus.FINISHED,
                true, 1, 1, null);
        Tournament tournament = Tournament.reconstruct(
                "t1", "TechCup", 4, BigDecimal.ZERO,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(10), LocalDate.now(),
                TournamentStatus.IN_PROGRESS, new ArrayList<>(), new ArrayList<>(List.of(finalMatch))
        );

        when(repository.findById("t1")).thenReturn(Optional.of(tournament));

        AssignChampionService service = new AssignChampionService(repository);

        assertThrows(ChampionPendingPenaltiesException.class,
                () -> service.assignChampion("t1", "final-1"));
    }

    @Test
    void assignChampion_whenTournamentNotFound_throwsException() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findById("missing")).thenReturn(Optional.empty());

        AssignChampionService service = new AssignChampionService(repository);

        assertThrows(TournamentNotFoundException.class,
                () -> service.assignChampion("missing", "final-1"));
    }
}
