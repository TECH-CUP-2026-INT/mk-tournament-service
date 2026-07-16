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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentChampionTest {

    private final UUID homeTeamId = UUID.randomUUID();
    private final UUID awayTeamId = UUID.randomUUID();

    private Tournament buildTournamentWithMatches(List<Match> matches) {
        return Tournament.reconstruct(
                UUID.randomUUID(), "TechCup", 4, BigDecimal.ZERO,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(10),
                LocalDate.now(),
                TournamentStatus.IN_PROGRESS,
                new ArrayList<>(),
                new ArrayList<>(matches)
        );
    }

    @Test
    void assignChampion_whenFinalMatchFinishedWithoutTie_assignsByRegulationTime() {
        UUID matchId = UUID.randomUUID();
        Match finalMatch = new Match(matchId, homeTeamId, awayTeamId, MatchStatus.FINISHED,
                true, 2, 1, null);
        Tournament tournament = buildTournamentWithMatches(List.of(finalMatch));

        ChampionAssignment assignment = tournament.assignChampionWhenFinalMatchFinished(matchId);

        assertEquals(homeTeamId, assignment.championTeamId());
        assertEquals(ChampionResolution.REGULATION_TIME, assignment.resolution());
        assertEquals(homeTeamId, tournament.getChampionTeamId());
        assertEquals(ChampionResolution.REGULATION_TIME, tournament.getChampionResolution());
    }

    @Test
    void assignChampion_whenFinalMatchTiedWithPenalties_assignsByPenalties() {
        UUID matchId = UUID.randomUUID();
        Match finalMatch = new Match(matchId, homeTeamId, awayTeamId, MatchStatus.FINISHED,
                true, 1, 1, awayTeamId);
        Tournament tournament = buildTournamentWithMatches(List.of(finalMatch));

        ChampionAssignment assignment = tournament.assignChampionWhenFinalMatchFinished(matchId);

        assertEquals(awayTeamId, assignment.championTeamId());
        assertEquals(ChampionResolution.PENALTIES, assignment.resolution());
    }

    @Test
    void assignChampion_whenFinalMatchTiedWithoutPenalties_throwsPendingPenalties() {
        UUID matchId = UUID.randomUUID();
        Match finalMatch = new Match(matchId, homeTeamId, awayTeamId, MatchStatus.FINISHED,
                true, 1, 1, null);
        Tournament tournament = buildTournamentWithMatches(List.of(finalMatch));

        assertThrows(ChampionPendingPenaltiesException.class,
                () -> tournament.assignChampionWhenFinalMatchFinished(matchId));
    }

    @Test
    void assignChampion_whenMatchIsNotFinal_throwsNotAllowed() {
        UUID matchId = UUID.randomUUID();
        Match semifinal = new Match(matchId, homeTeamId, awayTeamId, MatchStatus.FINISHED,
                false, 2, 0, null);
        Tournament tournament = buildTournamentWithMatches(List.of(semifinal));

        assertThrows(ChampionAssignmentNotAllowedException.class,
                () -> tournament.assignChampionWhenFinalMatchFinished(matchId));
    }

    @Test
    void assignChampion_whenFinalMatchNotFinished_throwsNotAllowed() {
        UUID matchId = UUID.randomUUID();
        Match finalMatch = new Match(matchId, homeTeamId, awayTeamId, MatchStatus.IN_PROGRESS,
                true, 0, 0, null);
        Tournament tournament = buildTournamentWithMatches(List.of(finalMatch));

        assertThrows(ChampionAssignmentNotAllowedException.class,
                () -> tournament.assignChampionWhenFinalMatchFinished(matchId));
    }

    @Test
    void match_finish_whenNotFinalMatch_throwsNotAllowed() {
        Match match = new Match(UUID.randomUUID(), homeTeamId, awayTeamId, MatchStatus.PENDING);

        assertThrows(ChampionAssignmentNotAllowedException.class,
                () -> match.finish(1, 0));
    }

    @Test
    void match_recordPenaltyShootout_whenNotTied_throwsNotAllowed() {
        Match match = new Match(UUID.randomUUID(), homeTeamId, awayTeamId, MatchStatus.FINISHED,
                true, 2, 1, null);

        assertThrows(ChampionAssignmentNotAllowedException.class,
                () -> match.recordPenaltyShootoutWinner(homeTeamId));
    }
}
