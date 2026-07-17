package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.MatchNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.BracketNode;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.domain.model.GroupTable;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Round;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.GroupStandingsCalculator;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ProcessMatchResultUseCase.ProcessMatchResultCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RecordMatchFinishedForSanctionsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.RecognitionAwardPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentEventPublisherPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessMatchResultServiceTest {

    private final TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
    private final RecordMatchFinishedForSanctionsUseCase recordMatchFinishedForSanctions =
            mock(RecordMatchFinishedForSanctionsUseCase.class);
    private final RecognitionAwardPort recognitionAwardPort = mock(RecognitionAwardPort.class);
    private final TournamentEventPublisherPort tournamentEventPublisher = mock(TournamentEventPublisherPort.class);
    private final ProcessMatchResultService service = new ProcessMatchResultService(
            repository, recordMatchFinishedForSanctions, recognitionAwardPort, tournamentEventPublisher);

    private Tournament buildTournament(UUID id, TournamentStatus status, List<Match> matches) {
        return Tournament.builder()
                .id(id).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(status).teams(List.of()).matches(new ArrayList<>(matches))
                .reconstruct();
    }

    private Match groupMatch(UUID id, UUID home, UUID away, String group) {
        return Match.builder().matchId(id).homeTeamId(home).awayTeamId(away)
                .status(MatchStatus.PENDING).groupName(group).phase(MatchPhase.GRUPOS).build();
    }

    // --- Guardas de entrada ---

    @Test
    void process_torneoNoExiste_lanzaTournamentNotFoundException() {
        UUID tournamentId = UUID.randomUUID();
        when(repository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> service.process(
                new ProcessMatchResultCommand(UUID.randomUUID(), tournamentId, MatchPhase.GRUPOS, 1, 0, null, null)));
    }

    @Test
    void process_partidoNoExiste_lanzaMatchNotFoundException() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = buildTournament(tournamentId, TournamentStatus.IN_PROGRESS, List.of());
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        assertThrows(MatchNotFoundException.class, () -> service.process(
                new ProcessMatchResultCommand(UUID.randomUUID(), tournamentId, MatchPhase.GRUPOS, 1, 0, null, null)));
    }

    @Test
    void process_torneoPausado_noProcesaNiInteractuaConSanciones() {
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        Match match = groupMatch(matchId, UUID.randomUUID(), UUID.randomUUID(), "Grupo A");
        Tournament tournament = buildTournament(tournamentId, TournamentStatus.IN_PROGRESS, List.of(match));
        tournament.pause();
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        service.process(new ProcessMatchResultCommand(matchId, tournamentId, MatchPhase.GRUPOS, 2, 0, null, null));

        assertEquals(MatchStatus.PENDING, match.getStatus());
        verify(recordMatchFinishedForSanctions, never()).recordMatchFinished();
        verify(repository, never()).save(any());
    }

    @Test
    void process_torneoInactivo_noProcesa() {
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        Match match = groupMatch(matchId, UUID.randomUUID(), UUID.randomUUID(), "Grupo A");
        Tournament tournament = buildTournament(tournamentId, TournamentStatus.IN_PROGRESS, List.of(match));
        tournament.inactivate();
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        service.process(new ProcessMatchResultCommand(matchId, tournamentId, MatchPhase.GRUPOS, 2, 0, null, null));

        assertEquals(MatchStatus.PENDING, match.getStatus());
        verify(repository, never()).save(any());
    }

    @Test
    void process_torneoYaFinalizado_noProcesa() {
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        Match match = groupMatch(matchId, UUID.randomUUID(), UUID.randomUUID(), "Grupo A");
        Tournament tournament = buildTournament(tournamentId, TournamentStatus.FINISHED, List.of(match));
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        service.process(new ProcessMatchResultCommand(matchId, tournamentId, MatchPhase.GRUPOS, 2, 0, null, null));

        assertEquals(MatchStatus.PENDING, match.getStatus());
        verify(repository, never()).save(any());
    }

    @Test
    void process_partidoYaFinalizado_esIdempotenteYNoLoReprocesa() {
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        UUID home = UUID.randomUUID();
        UUID away = UUID.randomUUID();
        Match match = Match.builder().matchId(matchId).homeTeamId(home).awayTeamId(away)
                .status(MatchStatus.FINISHED).homeScore(3).awayScore(0).groupName("Grupo A")
                .phase(MatchPhase.GRUPOS).build();
        Tournament tournament = buildTournament(tournamentId, TournamentStatus.IN_PROGRESS, List.of(match));
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        service.process(new ProcessMatchResultCommand(matchId, tournamentId, MatchPhase.GRUPOS, 1, 1, null, null));

        assertEquals(3, match.getHomeScore());
        assertEquals(0, match.getAwayScore());
        verify(recordMatchFinishedForSanctions, never()).recordMatchFinished();
        verify(repository, never()).save(any());
    }

    @Test
    void process_walkoverSobreUnPartidoYaWalkover_esIdempotente() {
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        UUID home = UUID.randomUUID();
        UUID away = UUID.randomUUID();
        Match match = Match.builder().matchId(matchId).homeTeamId(home).awayTeamId(away)
                .status(MatchStatus.FINISHED_NO_SHOW).groupName("Grupo A")
                .phase(MatchPhase.GRUPOS).absentTeamId(away).build();
        Tournament tournament = buildTournament(tournamentId, TournamentStatus.IN_PROGRESS, List.of(match));
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        service.process(new ProcessMatchResultCommand(matchId, tournamentId, MatchPhase.GRUPOS, 0, 0, home, away));

        verify(recordMatchFinishedForSanctions, never()).recordMatchFinished();
        verify(repository, never()).save(any());
    }

    // --- Fase GRUPOS ---

    @Test
    void process_partidoDeGrupoDecisivo_grabaResultadoYDescuentaSanciones() {
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        UUID home = UUID.randomUUID();
        UUID away = UUID.randomUUID();
        Match match = groupMatch(matchId, home, away, "Grupo A");
        Match otherPending = groupMatch(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "Grupo A");
        Tournament tournament = buildTournament(tournamentId, TournamentStatus.IN_PROGRESS, List.of(match, otherPending));
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        service.process(new ProcessMatchResultCommand(matchId, tournamentId, MatchPhase.GRUPOS, 2, 1, home, null));

        assertEquals(MatchStatus.FINISHED, match.getStatus());
        assertEquals(2, match.getHomeScore());
        assertEquals(1, match.getAwayScore());
        assertEquals(0, tournament.getBracketNodes().size());
        verify(recordMatchFinishedForSanctions).recordMatchFinished();
        verify(repository).save(tournament);
        verify(recognitionAwardPort, never()).triggerAwards(any());
        verify(tournamentEventPublisher, never()).publishTournamentFinalized(any());
    }

    @Test
    void process_partidoDeGrupoConAusente_marcaFinishedNoShowYLaTablaAcreditaAlPresente() {
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        UUID present = UUID.randomUUID();
        UUID absent = UUID.randomUUID();
        Match match = groupMatch(matchId, present, absent, "Grupo A");
        Tournament tournament = buildTournament(tournamentId, TournamentStatus.IN_PROGRESS, List.of(match));
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        // golesA/golesB vienen en 0 desde Matches para un walkover; no deben afectar la tabla.
        service.process(new ProcessMatchResultCommand(matchId, tournamentId, MatchPhase.GRUPOS, 0, 0, present, absent));

        assertEquals(MatchStatus.FINISHED_NO_SHOW, match.getStatus());
        assertEquals(absent, match.getAbsentTeamId());
        verify(recordMatchFinishedForSanctions).recordMatchFinished();

        List<GroupTable> tables = GroupStandingsCalculator.computeAll(tournament.getMatches(), Set.of());
        var presentStanding = tables.get(0).standings().stream()
                .filter(s -> s.teamId().equals(present)).findFirst().orElseThrow();
        var absentStanding = tables.get(0).standings().stream()
                .filter(s -> s.teamId().equals(absent)).findFirst().orElseThrow();
        assertEquals(3, presentStanding.points());
        assertEquals(0, presentStanding.goalsFor());
        assertEquals(0, absentStanding.points());
    }

    @Test
    void process_ultimoPartidoDeGrupos_generaLaLlaveEliminatoria() {
        UUID tournamentId = UUID.randomUUID();
        UUID a1 = UUID.randomUUID(); UUID a2 = UUID.randomUUID(); UUID a3 = UUID.randomUUID(); UUID a4 = UUID.randomUUID();
        UUID b1 = UUID.randomUUID(); UUID b2 = UUID.randomUUID(); UUID b3 = UUID.randomUUID(); UUID b4 = UUID.randomUUID();

        UUID lastMatchId = UUID.randomUUID();
        Match lastMatch = groupMatch(lastMatchId, b3, b4, "Grupo B");
        List<Match> matches = new ArrayList<>(fullyFinishedGroup("Grupo A", a1, a2, a3, a4));
        matches.addAll(List.of(
                finished(b1, b2, 3, 0, "Grupo B"),
                finished(b1, b3, 3, 0, "Grupo B"),
                finished(b1, b4, 3, 0, "Grupo B"),
                finished(b2, b3, 2, 0, "Grupo B"),
                finished(b2, b4, 2, 0, "Grupo B"),
                lastMatch));

        Tournament tournament = buildTournament(tournamentId, TournamentStatus.IN_PROGRESS, matches);
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        service.process(new ProcessMatchResultCommand(lastMatchId, tournamentId, MatchPhase.GRUPOS, 1, 0, b3, null));

        assertEquals(3, tournament.getBracketNodes().size());
        verify(repository).save(tournament);
    }

    // --- Fase ELIMINATORIA ---

    private Tournament twoGroupBracketTournament(UUID tournamentId) {
        UUID a1 = UUID.randomUUID(); UUID a2 = UUID.randomUUID(); UUID a3 = UUID.randomUUID(); UUID a4 = UUID.randomUUID();
        UUID b1 = UUID.randomUUID(); UUID b2 = UUID.randomUUID(); UUID b3 = UUID.randomUUID(); UUID b4 = UUID.randomUUID();
        List<Match> matches = new ArrayList<>();
        matches.addAll(fullyFinishedGroup("Grupo A", a1, a2, a3, a4));
        matches.addAll(fullyFinishedGroup("Grupo B", b1, b2, b3, b4));
        Tournament tournament = buildTournament(tournamentId, TournamentStatus.IN_PROGRESS, matches);
        tournament.generateEliminationBracket();
        return tournament;
    }

    @Test
    void process_partidoDeSemifinal_avanzaLaLlaveSinFinalizarElTorneo() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = twoGroupBracketTournament(tournamentId);
        BracketNode semi1 = tournament.getBracketNodes().stream()
                .filter(n -> n.getRound() == Round.SEMIFINAL).findFirst().orElseThrow();
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        service.process(new ProcessMatchResultCommand(
                semi1.getMatchId(), tournamentId, MatchPhase.ELIMINATORIA, 2, 0, semi1.getSlotA(), null));

        assertEquals(TournamentStatus.IN_PROGRESS, tournament.getStatus());
        assertEquals(semi1.getSlotA(), semi1.getWinnerTeamId());
        verify(recognitionAwardPort, never()).triggerAwards(any());
        verify(tournamentEventPublisher, never()).publishTournamentFinalized(any());
    }

    @Test
    void process_semifinalConAusente_marcaWalkoverYAvanzaAlPresente() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = twoGroupBracketTournament(tournamentId);
        BracketNode semi1 = tournament.getBracketNodes().stream()
                .filter(n -> n.getRound() == Round.SEMIFINAL).findFirst().orElseThrow();
        UUID present = semi1.getSlotA();
        UUID absent = semi1.getSlotB();
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        service.process(new ProcessMatchResultCommand(
                semi1.getMatchId(), tournamentId, MatchPhase.ELIMINATORIA, 0, 0, present, absent));

        Match match = tournament.getMatches().stream()
                .filter(m -> m.getMatchId().equals(semi1.getMatchId())).findFirst().orElseThrow();
        assertEquals(MatchStatus.FINISHED_NO_SHOW, match.getStatus());
        assertEquals(absent, match.getAbsentTeamId());
        assertEquals(present, semi1.getWinnerTeamId());
        assertEquals(absent, semi1.getLoserTeamId());
    }

    @Test
    void process_resuelveLaFinal_finalizaElTorneoYDisparaEfectosSecundarios() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = twoGroupBracketTournament(tournamentId);
        List<BracketNode> semis = tournament.getBracketNodes().stream()
                .filter(n -> n.getRound() == Round.SEMIFINAL).toList();
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        resolve(tournament, semis.get(0).getMatchId(), semis.get(0).getSlotA());
        resolve(tournament, semis.get(1).getMatchId(), semis.get(1).getSlotB());

        BracketNode finalNode = tournament.getBracketNodes().stream()
                .filter(n -> n.getRound() == Round.FINAL).findFirst().orElseThrow();
        UUID championId = finalNode.getSlotA();

        service.process(new ProcessMatchResultCommand(
                finalNode.getMatchId(), tournamentId, MatchPhase.ELIMINATORIA, 2, 0, championId, null));

        assertEquals(TournamentStatus.FINISHED, tournament.getStatus());
        assertEquals(championId, tournament.getChampionTeamId());
        assertEquals(ChampionResolution.REGULATION_TIME, tournament.getChampionResolution());
        verify(recognitionAwardPort).triggerAwards(tournamentId);
        verify(tournamentEventPublisher).publishTournamentFinalized(tournamentId);
    }

    @Test
    void process_finalConAusente_asignaCampeonPorWalkover() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = twoGroupBracketTournament(tournamentId);
        List<BracketNode> semis = tournament.getBracketNodes().stream()
                .filter(n -> n.getRound() == Round.SEMIFINAL).toList();
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        resolve(tournament, semis.get(0).getMatchId(), semis.get(0).getSlotA());
        resolve(tournament, semis.get(1).getMatchId(), semis.get(1).getSlotB());

        BracketNode finalNode = tournament.getBracketNodes().stream()
                .filter(n -> n.getRound() == Round.FINAL).findFirst().orElseThrow();
        UUID championId = finalNode.getSlotA();
        UUID absentRunnerUp = finalNode.getSlotB();

        service.process(new ProcessMatchResultCommand(
                finalNode.getMatchId(), tournamentId, MatchPhase.ELIMINATORIA, 0, 0, championId, absentRunnerUp));

        assertEquals(TournamentStatus.FINISHED, tournament.getStatus());
        assertEquals(championId, tournament.getChampionTeamId());
        assertEquals(absentRunnerUp, tournament.getRunnerUpTeamId());
        assertEquals(ChampionResolution.WALKOVER, tournament.getChampionResolution());
    }

    @Test
    void process_partidoEmpatadoSinGanadorExterno_marcaPendienteDePenalesSinExplotar() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = twoGroupBracketTournament(tournamentId);
        BracketNode semi1 = tournament.getBracketNodes().stream()
                .filter(n -> n.getRound() == Round.SEMIFINAL).findFirst().orElseThrow();
        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        service.process(new ProcessMatchResultCommand(
                semi1.getMatchId(), tournamentId, MatchPhase.ELIMINATORIA, 1, 1, null, null));

        assertEquals(co.edu.escuelaing.techcup.tournament.domain.model.BracketNodeStatus.PENDING_PENALTIES,
                semi1.getStatus());
        verify(repository, times(1)).save(tournament);
    }

    private void resolve(Tournament tournament, UUID matchId, UUID winnerTeamId) {
        Match match = tournament.getMatches().stream()
                .filter(m -> m.getMatchId().equals(matchId)).findFirst().orElseThrow();
        match.finishWithExternalResult(2, 0, winnerTeamId);
        tournament.advanceBracket(matchId);
    }

    private List<Match> fullyFinishedGroup(String groupName, UUID t1, UUID t2, UUID t3, UUID t4) {
        return new ArrayList<>(List.of(
                finished(t1, t2, 3, 0, groupName),
                finished(t1, t3, 3, 0, groupName),
                finished(t1, t4, 3, 0, groupName),
                finished(t2, t3, 2, 0, groupName),
                finished(t2, t4, 2, 0, groupName),
                finished(t3, t4, 1, 0, groupName)));
    }

    private Match finished(UUID home, UUID away, int homeScore, int awayScore, String group) {
        return Match.builder().matchId(UUID.randomUUID()).homeTeamId(home).awayTeamId(away)
                .status(MatchStatus.FINISHED).homeScore(homeScore).awayScore(awayScore)
                .groupName(group).phase(MatchPhase.GRUPOS).build();
    }
}
