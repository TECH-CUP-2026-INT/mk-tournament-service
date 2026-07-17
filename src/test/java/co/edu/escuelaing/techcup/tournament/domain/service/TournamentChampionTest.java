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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentChampionTest {

    private final UUID homeTeamId = UUID.randomUUID();
    private final UUID awayTeamId = UUID.randomUUID();

    private Tournament buildTournamentWithMatches(List<Match> matches) {
        return Tournament.builder()
                .id(UUID.randomUUID()).name("TechCup").numberOfTeams(4).cost(BigDecimal.ZERO)
                .startDate(LocalDate.now().plusDays(2)).endDate(LocalDate.now().plusDays(10))
                .registrationDeadline(LocalDate.now())
                .status(TournamentStatus.IN_PROGRESS).teams(new ArrayList<>())
                .matches(new ArrayList<>(matches))
                .reconstruct();
    }

    @Test
    void assignChampion_whenFinalMatchFinishedWithoutTie_assignsByRegulationTime() {
        UUID matchId = UUID.randomUUID();
        Match finalMatch = Match.builder().matchId(matchId).homeTeamId(homeTeamId).awayTeamId(awayTeamId)
                .status(MatchStatus.FINISHED).finalMatch(true).homeScore(2).awayScore(1).build();
        Tournament tournament = buildTournamentWithMatches(List.of(finalMatch));

        ChampionAssignment assignment = tournament.assignChampionWhenFinalMatchFinished(matchId);

        assertEquals(homeTeamId, assignment.championTeamId());
        assertEquals(awayTeamId, assignment.runnerUpTeamId());
        assertEquals(ChampionResolution.REGULATION_TIME, assignment.resolution());
        assertEquals(homeTeamId, tournament.getChampionTeamId());
        assertEquals(awayTeamId, tournament.getRunnerUpTeamId());
        assertEquals(ChampionResolution.REGULATION_TIME, tournament.getChampionResolution());
    }

    @Test
    void assignChampion_whenFinalMatchTiedWithPenalties_assignsByPenalties() {
        UUID matchId = UUID.randomUUID();
        Match finalMatch = Match.builder().matchId(matchId).homeTeamId(homeTeamId).awayTeamId(awayTeamId)
                .status(MatchStatus.FINISHED).finalMatch(true).homeScore(1).awayScore(1)
                .penaltyShootoutWinnerTeamId(awayTeamId).build();
        Tournament tournament = buildTournamentWithMatches(List.of(finalMatch));

        ChampionAssignment assignment = tournament.assignChampionWhenFinalMatchFinished(matchId);

        assertEquals(awayTeamId, assignment.championTeamId());
        assertEquals(homeTeamId, assignment.runnerUpTeamId());
        assertEquals(ChampionResolution.PENALTIES, assignment.resolution());
    }

    @Test
    void assignChampion_whenFinalMatchTiedWithoutPenalties_throwsPendingPenalties() {
        UUID matchId = UUID.randomUUID();
        Match finalMatch = Match.builder().matchId(matchId).homeTeamId(homeTeamId).awayTeamId(awayTeamId)
                .status(MatchStatus.FINISHED).finalMatch(true).homeScore(1).awayScore(1).build();
        Tournament tournament = buildTournamentWithMatches(List.of(finalMatch));

        assertThrows(ChampionPendingPenaltiesException.class,
                () -> tournament.assignChampionWhenFinalMatchFinished(matchId));
    }

    @Test
    void assignChampion_whenMatchIsNotFinal_throwsNotAllowed() {
        UUID matchId = UUID.randomUUID();
        Match semifinal = Match.builder().matchId(matchId).homeTeamId(homeTeamId).awayTeamId(awayTeamId)
                .status(MatchStatus.FINISHED).finalMatch(false).homeScore(2).awayScore(0).build();
        Tournament tournament = buildTournamentWithMatches(List.of(semifinal));

        assertThrows(ChampionAssignmentNotAllowedException.class,
                () -> tournament.assignChampionWhenFinalMatchFinished(matchId));
    }

    @Test
    void assignChampion_whenFinalMatchNotFinished_throwsNotAllowed() {
        UUID matchId = UUID.randomUUID();
        Match finalMatch = Match.builder().matchId(matchId).homeTeamId(homeTeamId).awayTeamId(awayTeamId)
                .status(MatchStatus.IN_PROGRESS).finalMatch(true).homeScore(0).awayScore(0).build();
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
        Match match = Match.builder().matchId(UUID.randomUUID()).homeTeamId(homeTeamId).awayTeamId(awayTeamId)
                .status(MatchStatus.FINISHED).finalMatch(true).homeScore(2).awayScore(1).build();

        assertThrows(ChampionAssignmentNotAllowedException.class,
                () -> match.recordPenaltyShootoutWinner(homeTeamId));
    }

    @Test
    void match_finishWithExternalResult_noExigeFinalMatch() {
        Match match = new Match(UUID.randomUUID(), homeTeamId, awayTeamId, MatchStatus.PENDING);

        match.finishWithExternalResult(2, 0, homeTeamId);

        assertEquals(MatchStatus.FINISHED, match.getStatus());
        assertEquals(2, match.getHomeScore());
        assertEquals(0, match.getAwayScore());
        assertEquals(homeTeamId, match.resolveChampionTeamId());
    }

    @Test
    void match_finishWithExternalResult_empatadoConGanadorExterno_loGuardaComoGanadorDePenales() {
        Match match = new Match(UUID.randomUUID(), homeTeamId, awayTeamId, MatchStatus.PENDING);

        match.finishWithExternalResult(1, 1, awayTeamId);

        assertEquals(awayTeamId, match.getPenaltyShootoutWinnerTeamId());
        assertEquals(awayTeamId, match.resolveChampionTeamId());
    }

    @Test
    void match_finishWithExternalResult_empatadoSinGanador_quedaPendienteDeResolucion() {
        Match match = new Match(UUID.randomUUID(), homeTeamId, awayTeamId, MatchStatus.PENDING);

        match.finishWithExternalResult(1, 1, null);

        assertEquals(MatchStatus.FINISHED, match.getStatus());
        assertNull(match.getPenaltyShootoutWinnerTeamId());
        assertNull(match.resolveChampionTeamId());
    }

    @Test
    void match_finishWithExternalResult_ganadorQueNoEsDelPartido_lanzaExcepcion() {
        Match match = new Match(UUID.randomUUID(), homeTeamId, awayTeamId, MatchStatus.PENDING);

        assertThrows(ChampionAssignmentNotAllowedException.class,
                () -> match.finishWithExternalResult(1, 1, UUID.randomUUID()));
    }
}
