package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.exception.BracketNodeNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.EliminationBracketAlreadyGeneratedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.GroupStageNotCompleteException;
import co.edu.escuelaing.techcup.tournament.domain.model.BracketNode;
import co.edu.escuelaing.techcup.tournament.domain.model.BracketNodeStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Round;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TournamentEliminationBracketTest {

    private Tournament buildTournament(List<Match> matches) {
        return Tournament.builder()
                .id(UUID.randomUUID()).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(TournamentStatus.IN_PROGRESS).teams(List.of()).matches(new ArrayList<>(matches))
                .reconstruct();
    }

    private Match finishedGroupMatch(UUID home, UUID away, int homeScore, int awayScore, String group) {
        return Match.builder().matchId(UUID.randomUUID()).homeTeamId(home).awayTeamId(away)
                .status(MatchStatus.FINISHED).homeScore(homeScore).awayScore(awayScore).groupName(group).build();
    }

    /** Round robin de 4 equipos con orden de posiciones clarísimo: t1 &gt; t2 &gt; t3 &gt; t4. */
    private List<Match> fullyFinishedGroup(String groupName, UUID t1, UUID t2, UUID t3, UUID t4) {
        return new ArrayList<>(List.of(
                finishedGroupMatch(t1, t2, 3, 0, groupName),
                finishedGroupMatch(t1, t3, 3, 0, groupName),
                finishedGroupMatch(t1, t4, 3, 0, groupName),
                finishedGroupMatch(t2, t3, 2, 0, groupName),
                finishedGroupMatch(t2, t4, 2, 0, groupName),
                finishedGroupMatch(t3, t4, 1, 0, groupName)));
    }

    private BracketNode findByRound(Tournament tournament, Round round, int index) {
        return tournament.getBracketNodes().stream()
                .filter(n -> n.getRound() == round)
                .toList()
                .get(index);
    }

    private Optional<BracketNode> nodeForMatch(Tournament tournament, UUID matchId) {
        return tournament.getBracketNodes().stream().filter(n -> matchId.equals(n.getMatchId())).findFirst();
    }

    // --- Generación ---

    @Test
    void generateEliminationBracket_conDosGruposCompletos_sembraSemifinalesCruzadas() {
        UUID a1 = UUID.randomUUID(); UUID a2 = UUID.randomUUID(); UUID a3 = UUID.randomUUID(); UUID a4 = UUID.randomUUID();
        UUID b1 = UUID.randomUUID(); UUID b2 = UUID.randomUUID(); UUID b3 = UUID.randomUUID(); UUID b4 = UUID.randomUUID();
        List<Match> matches = new ArrayList<>();
        matches.addAll(fullyFinishedGroup("Grupo A", a1, a2, a3, a4));
        matches.addAll(fullyFinishedGroup("Grupo B", b1, b2, b3, b4));
        Tournament tournament = buildTournament(matches);

        tournament.generateEliminationBracket();

        assertEquals(3, tournament.getBracketNodes().size());
        List<BracketNode> semis = tournament.getBracketNodes().stream()
                .filter(n -> n.getRound() == Round.SEMIFINAL).toList();
        assertEquals(2, semis.size());

        // 1A-2B y 1B-2A (a1 y b1 son los 1eros, a2 y b2 los 2dos de cada grupo).
        boolean crossSeeded = semis.stream().anyMatch(n -> n.getSlotA().equals(a1) && n.getSlotB().equals(b2))
                && semis.stream().anyMatch(n -> n.getSlotA().equals(b1) && n.getSlotB().equals(a2));
        assertTrue(crossSeeded, "La primera ronda debe sembrarse cruzada (1A-2B, 1B-2A)");

        semis.forEach(n -> {
            assertEquals(BracketNodeStatus.SCHEDULED, n.getStatus());
            assertTrue(tournament.getMatches().stream().anyMatch(m -> m.getMatchId().equals(n.getMatchId())));
        });

        BracketNode finalNode = findByRound(tournament, Round.FINAL, 0);
        assertNull(finalNode.getSlotA());
        assertNull(finalNode.getSlotB());
        assertNull(finalNode.getMatchId());
        assertEquals(BracketNodeStatus.PENDING_SLOTS, finalNode.getStatus());
        assertNull(finalNode.getAdvanceToNodeId());

        semis.forEach(n -> assertEquals(finalNode.getNodeId(), n.getAdvanceToNodeId()));
    }

    @Test
    void generateEliminationBracket_conCuatroGrupos_armaCuartosSemisYFinalSinRematchesTempranos() {
        List<Match> matches = new ArrayList<>();
        List<UUID[]> groupTeams = new ArrayList<>();
        for (String groupName : List.of("Grupo A", "Grupo B", "Grupo C", "Grupo D")) {
            UUID[] teams = {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};
            groupTeams.add(teams);
            matches.addAll(fullyFinishedGroup(groupName, teams[0], teams[1], teams[2], teams[3]));
        }
        Tournament tournament = buildTournament(matches);

        tournament.generateEliminationBracket();

        assertEquals(7, tournament.getBracketNodes().size());
        assertEquals(4, tournament.getBracketNodes().stream().filter(n -> n.getRound() == Round.QUARTERFINAL).count());
        assertEquals(2, tournament.getBracketNodes().stream().filter(n -> n.getRound() == Round.SEMIFINAL).count());
        assertEquals(1, tournament.getBracketNodes().stream().filter(n -> n.getRound() == Round.FINAL).count());

        UUID a1 = groupTeams.get(0)[0];
        UUID c1 = groupTeams.get(2)[0];
        BracketNode nodeA = tournament.getBracketNodes().stream()
                .filter(n -> n.getRound() == Round.QUARTERFINAL && a1.equals(n.getSlotA())).findFirst().orElseThrow();
        BracketNode nodeC = tournament.getBracketNodes().stream()
                .filter(n -> n.getRound() == Round.QUARTERFINAL && c1.equals(n.getSlotA())).findFirst().orElseThrow();

        // 1A-2B y 1C-2D deben cruzarse entre sí en semis (no 1A-2B con 1B-2A).
        assertEquals(nodeA.getAdvanceToNodeId(), nodeC.getAdvanceToNodeId());
    }

    @Test
    void generateEliminationBracket_conGruposSinTerminar_lanzaGroupStageNotCompleteException() {
        UUID a1 = UUID.randomUUID(); UUID a2 = UUID.randomUUID(); UUID a3 = UUID.randomUUID(); UUID a4 = UUID.randomUUID();
        List<Match> matches = new ArrayList<>(fullyFinishedGroup("Grupo A", a1, a2, a3, a4));
        matches.add(Match.builder().matchId(UUID.randomUUID()).homeTeamId(a1).awayTeamId(a2)
                .status(MatchStatus.PENDING).groupName("Grupo B").build());
        Tournament tournament = buildTournament(matches);

        assertThrows(GroupStageNotCompleteException.class, tournament::generateEliminationBracket);
    }

    @Test
    void generateEliminationBracket_sinPartidosDeGrupo_lanzaGroupStageNotCompleteException() {
        Tournament tournament = buildTournament(List.of());

        assertThrows(GroupStageNotCompleteException.class, tournament::generateEliminationBracket);
    }

    @Test
    void generateEliminationBracket_yaGenerada_lanzaEliminationBracketAlreadyGeneratedException() {
        UUID a1 = UUID.randomUUID(); UUID a2 = UUID.randomUUID(); UUID a3 = UUID.randomUUID(); UUID a4 = UUID.randomUUID();
        UUID b1 = UUID.randomUUID(); UUID b2 = UUID.randomUUID(); UUID b3 = UUID.randomUUID(); UUID b4 = UUID.randomUUID();
        List<Match> matches = new ArrayList<>();
        matches.addAll(fullyFinishedGroup("Grupo A", a1, a2, a3, a4));
        matches.addAll(fullyFinishedGroup("Grupo B", b1, b2, b3, b4));
        Tournament tournament = buildTournament(matches);
        tournament.generateEliminationBracket();

        assertThrows(EliminationBracketAlreadyGeneratedException.class, tournament::generateEliminationBracket);
    }

    // --- Avance ---

    private Tournament twoGroupBracket() {
        UUID a1 = UUID.randomUUID(); UUID a2 = UUID.randomUUID(); UUID a3 = UUID.randomUUID(); UUID a4 = UUID.randomUUID();
        UUID b1 = UUID.randomUUID(); UUID b2 = UUID.randomUUID(); UUID b3 = UUID.randomUUID(); UUID b4 = UUID.randomUUID();
        List<Match> matches = new ArrayList<>();
        matches.addAll(fullyFinishedGroup("Grupo A", a1, a2, a3, a4));
        matches.addAll(fullyFinishedGroup("Grupo B", b1, b2, b3, b4));
        Tournament tournament = buildTournament(matches);
        tournament.generateEliminationBracket();
        return tournament;
    }

    @Test
    void advanceBracket_ganadorResuelto_subeAlSiguienteNodoYQuedaPendienteHastaLlenarLosDosCupos() {
        Tournament tournament = twoGroupBracket();
        BracketNode semi1 = findByRound(tournament, Round.SEMIFINAL, 0);
        Match semi1Match = tournament.getMatches().stream()
                .filter(m -> m.getMatchId().equals(semi1.getMatchId())).findFirst().orElseThrow();

        semi1Match.finishWithExternalResult(2, 1, semi1.getSlotA());
        tournament.advanceBracket(semi1.getMatchId());

        BracketNode finalNode = findByRound(tournament, Round.FINAL, 0);
        assertEquals(BracketNodeStatus.FINISHED, semi1.getStatus());
        assertEquals(semi1.getSlotA(), semi1.getWinnerTeamId());
        assertEquals(BracketNodeStatus.PENDING_SLOTS, finalNode.getStatus());
        assertNull(finalNode.getMatchId());
        assertEquals(semi1.getSlotA(), finalNode.getSlotA());
        assertNull(finalNode.getSlotB());
    }

    @Test
    void advanceBracket_ambosCuposDeSemifinalesResueltos_creaElPartidoDeLaFinal() {
        Tournament tournament = twoGroupBracket();
        BracketNode semi1 = findByRound(tournament, Round.SEMIFINAL, 0);
        BracketNode semi2 = findByRound(tournament, Round.SEMIFINAL, 1);

        resolveMatch(tournament, semi1.getMatchId(), semi1.getSlotA());
        resolveMatch(tournament, semi2.getMatchId(), semi2.getSlotB());

        BracketNode finalNode = findByRound(tournament, Round.FINAL, 0);
        assertEquals(BracketNodeStatus.SCHEDULED, finalNode.getStatus());
        assertTrue(finalNode.getMatchId() != null);
        Match finalMatch = tournament.getMatches().stream()
                .filter(m -> m.getMatchId().equals(finalNode.getMatchId())).findFirst().orElseThrow();
        assertTrue(finalMatch.isFinalMatch());
        assertEquals(semi1.getSlotA(), finalMatch.getHomeTeamId());
        assertEquals(semi2.getSlotB(), finalMatch.getAwayTeamId());
    }

    @Test
    void advanceBracket_nodoEraLaFinal_asignaCampeonYSubcampeonYFinalizaElTorneo() {
        Tournament tournament = twoGroupBracket();
        BracketNode semi1 = findByRound(tournament, Round.SEMIFINAL, 0);
        BracketNode semi2 = findByRound(tournament, Round.SEMIFINAL, 1);
        resolveMatch(tournament, semi1.getMatchId(), semi1.getSlotA());
        resolveMatch(tournament, semi2.getMatchId(), semi2.getSlotB());

        BracketNode finalNode = findByRound(tournament, Round.FINAL, 0);
        UUID championId = finalNode.getSlotA();
        UUID runnerUpId = finalNode.getSlotB();
        resolveMatch(tournament, finalNode.getMatchId(), championId);

        assertEquals(TournamentStatus.FINISHED, tournament.getStatus());
        assertEquals(championId, tournament.getChampionTeamId());
        assertEquals(runnerUpId, tournament.getRunnerUpTeamId());
        assertEquals(ChampionResolution.REGULATION_TIME, tournament.getChampionResolution());
        assertEquals(BracketNodeStatus.FINISHED, finalNode.getStatus());
    }

    @Test
    void advanceBracket_esIdempotente_siYaEstabaFinalizadoNoLoVuelveAResolver() {
        Tournament tournament = twoGroupBracket();
        BracketNode semi1 = findByRound(tournament, Round.SEMIFINAL, 0);
        resolveMatch(tournament, semi1.getMatchId(), semi1.getSlotA());

        UUID winnerBefore = semi1.getWinnerTeamId();
        // Reintento del mismo evento (idempotencia): no debe fallar ni recalcular.
        tournament.advanceBracket(semi1.getMatchId());

        assertEquals(winnerBefore, semi1.getWinnerTeamId());
        assertEquals(BracketNodeStatus.FINISHED, semi1.getStatus());
    }

    @Test
    void advanceBracket_empateSinGanadorExterno_marcaPendienteDePenalesYNoAvanza() {
        Tournament tournament = twoGroupBracket();
        BracketNode semi1 = findByRound(tournament, Round.SEMIFINAL, 0);
        Match semi1Match = tournament.getMatches().stream()
                .filter(m -> m.getMatchId().equals(semi1.getMatchId())).findFirst().orElseThrow();

        semi1Match.finishWithExternalResult(1, 1, null);
        tournament.advanceBracket(semi1.getMatchId());

        assertEquals(BracketNodeStatus.PENDING_PENALTIES, semi1.getStatus());
        assertNull(semi1.getWinnerTeamId());
        BracketNode finalNode = findByRound(tournament, Round.FINAL, 0);
        assertNull(finalNode.getSlotA());
        assertNull(finalNode.getSlotB());
    }

    @Test
    void recordPenaltyShootoutWinner_nodoPendienteDePenales_avanzaLaLlaveConElGanadorManual() {
        Tournament tournament = twoGroupBracket();
        BracketNode semi1 = findByRound(tournament, Round.SEMIFINAL, 0);
        Match semi1Match = tournament.getMatches().stream()
                .filter(m -> m.getMatchId().equals(semi1.getMatchId())).findFirst().orElseThrow();
        semi1Match.finishWithExternalResult(1, 1, null);
        tournament.advanceBracket(semi1.getMatchId());
        assertEquals(BracketNodeStatus.PENDING_PENALTIES, semi1.getStatus());

        tournament.recordPenaltyShootoutWinner(semi1.getMatchId(), semi1.getSlotA());

        assertEquals(BracketNodeStatus.FINISHED, semi1.getStatus());
        assertEquals(semi1.getSlotA(), semi1.getWinnerTeamId());
        BracketNode finalNode = findByRound(tournament, Round.FINAL, 0);
        assertEquals(semi1.getSlotA(), finalNode.getSlotA());
    }

    @Test
    void advanceBracket_matchNoPerteneceALaLlave_lanzaBracketNodeNotFoundException() {
        Tournament tournament = twoGroupBracket();

        assertThrows(BracketNodeNotFoundException.class, () -> tournament.advanceBracket(UUID.randomUUID()));
    }

    private void resolveMatch(Tournament tournament, UUID matchId, UUID winnerTeamId) {
        Match match = tournament.getMatches().stream()
                .filter(m -> m.getMatchId().equals(matchId)).findFirst().orElseThrow();
        int winnerScore = 2;
        int loserScore = 0;
        if (winnerTeamId.equals(match.getHomeTeamId())) {
            match.finishWithExternalResult(winnerScore, loserScore, winnerTeamId);
        } else {
            match.finishWithExternalResult(loserScore, winnerScore, winnerTeamId);
        }
        tournament.advanceBracket(matchId);
    }
}
