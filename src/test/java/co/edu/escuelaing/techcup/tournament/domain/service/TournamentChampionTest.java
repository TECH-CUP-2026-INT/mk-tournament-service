package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionPendingPenaltiesException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentChampionTest {

    private Tournament buildTournamentWithMatches(List<Match> matches) {
        Tournament t = Tournament.reconstruct(
                "t1", "TechCup", 4, BigDecimal.ZERO,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10),
                LocalDate.now(),
                TournamentStatus.IN_PROGRESS,
                new ArrayList<>(),
                new ArrayList<>(matches)
        );
        return t;
    }

    @Test
    void assignChampion_whenFinalMatchFinishedWithoutTie_assignsByRegulationTime() {
        Match finalMatch = new Match("final-1", "home", "away", MatchStatus.FINISHED,
                true, 2, 1, null);
        Tournament tournament = buildTournamentWithMatches(List.of(finalMatch));

        ChampionAssignment assignment = tournament.assignChampionWhenFinalMatchFinished("final-1");

        assertEquals("home", assignment.championTeamId());
        assertEquals(ChampionResolution.REGULATION_TIME, assignment.resolution());
        assertEquals("home", tournament.getChampionTeamId());
        assertEquals(ChampionResolution.REGULATION_TIME, tournament.getChampionResolution());
    }

    @Test
    void assignChampion_whenFinalMatchTiedWithPenalties_assignsByPenalties() {
        Match finalMatch = new Match("final-1", "home", "away", MatchStatus.FINISHED,
                true, 1, 1, "away");
        Tournament tournament = buildTournamentWithMatches(List.of(finalMatch));

        ChampionAssignment assignment = tournament.assignChampionWhenFinalMatchFinished("final-1");

        assertEquals("away", assignment.championTeamId());
        assertEquals(ChampionResolution.PENALTIES, assignment.resolution());
    }

    @Test
    void assignChampion_whenFinalMatchTiedWithoutPenalties_throwsPendingPenalties() {
        Match finalMatch = new Match("final-1", "home", "away", MatchStatus.FINISHED,
                true, 1, 1, null);
        Tournament tournament = buildTournamentWithMatches(List.of(finalMatch));

        assertThrows(ChampionPendingPenaltiesException.class,
                () -> tournament.assignChampionWhenFinalMatchFinished("final-1"));
    }

    @Test
    void assignChampion_whenMatchIsNotFinal_throwsNotAllowed() {
        Match semifinal = new Match("semi-1", "home", "away", MatchStatus.FINISHED,
                false, 2, 0, null);
        Tournament tournament = buildTournamentWithMatches(List.of(semifinal));

        assertThrows(ChampionAssignmentNotAllowedException.class,
                () -> tournament.assignChampionWhenFinalMatchFinished("semi-1"));
    }

    @Test
    void assignChampion_whenFinalMatchNotFinished_throwsNotAllowed() {
        Match finalMatch = new Match("final-1", "home", "away", MatchStatus.IN_PROGRESS,
                true, 0, 0, null);
        Tournament tournament = buildTournamentWithMatches(List.of(finalMatch));

        assertThrows(ChampionAssignmentNotAllowedException.class,
                () -> tournament.assignChampionWhenFinalMatchFinished("final-1"));
    }

    @Test
    void match_finish_whenNotFinalMatch_throwsNotAllowed() {
        Match match = new Match("m1", "home", "away", MatchStatus.PENDING);

        assertThrows(ChampionAssignmentNotAllowedException.class,
                () -> match.finish(1, 0));
    }

    @Test
    void match_recordPenaltyShootout_whenNotTied_throwsNotAllowed() {
        Match match = new Match("final-1", "home", "away", MatchStatus.FINISHED,
                true, 2, 1, null);

        assertThrows(ChampionAssignmentNotAllowedException.class,
                () -> match.recordPenaltyShootoutWinner("home"));
    }
}
