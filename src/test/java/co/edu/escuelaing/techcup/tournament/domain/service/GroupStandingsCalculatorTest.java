package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.GroupStanding;
import co.edu.escuelaing.techcup.tournament.domain.model.GroupStandingsCalculator;
import co.edu.escuelaing.techcup.tournament.domain.model.GroupTable;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupStandingsCalculatorTest {

    private final UUID a = UUID.randomUUID();
    private final UUID b = UUID.randomUUID();
    private final UUID c = UUID.randomUUID();
    private final UUID d = UUID.randomUUID();

    private Match finished(UUID home, UUID away, int homeScore, int awayScore, String group) {
        return Match.builder().matchId(UUID.randomUUID()).homeTeamId(home).awayTeamId(away)
                .status(MatchStatus.FINISHED).homeScore(homeScore).awayScore(awayScore)
                .groupName(group).build();
    }

    private Match pending(UUID home, UUID away, String group) {
        return Match.builder().matchId(UUID.randomUUID()).homeTeamId(home).awayTeamId(away)
                .status(MatchStatus.PENDING).groupName(group).build();
    }

    private Match noShow(UUID home, UUID away, String group) {
        return Match.builder().matchId(UUID.randomUUID()).homeTeamId(home).awayTeamId(away)
                .status(MatchStatus.FINISHED_NO_SHOW).groupName(group).build();
    }

    private Match noShowWithAbsentTeam(UUID home, UUID away, UUID absentTeamId, String group) {
        return Match.builder().matchId(UUID.randomUUID()).homeTeamId(home).awayTeamId(away)
                .status(MatchStatus.FINISHED_NO_SHOW).absentTeamId(absentTeamId).groupName(group).build();
    }

    @Test
    void computeAll_ignoraPartidosSinGrupo() {
        Match noGroup = finished(a, b, 1, 0, null);

        List<GroupTable> tables = GroupStandingsCalculator.computeAll(List.of(noGroup), Set.of());

        assertTrue(tables.isEmpty());
    }

    @Test
    void computeAll_ordenaVariosGruposPorNombre() {
        Match matchB = finished(a, b, 1, 0, "Grupo B");
        Match matchA = finished(a, b, 1, 0, "Grupo A");

        List<GroupTable> tables = GroupStandingsCalculator.computeAll(List.of(matchB, matchA), Set.of());

        assertEquals("Grupo A", tables.get(0).groupName());
        assertEquals("Grupo B", tables.get(1).groupName());
    }

    @Test
    void table_equipoConPartidoPendiente_apareceConEstadisticasEnCero() {
        List<Match> matches = List.of(pending(a, b, "Grupo A"));

        List<GroupTable> tables = GroupStandingsCalculator.computeAll(matches, Set.of());

        List<GroupStanding> standings = tables.get(0).standings();
        assertEquals(2, standings.size());
        standings.forEach(s -> {
            assertEquals(0, s.played());
            assertEquals(0, s.points());
        });
    }

    @Test
    void table_calculaPuntosGolesYDiferenciaCorrectamente() {
        List<Match> matches = List.of(
                finished(a, b, 2, 1, "Grupo A"),
                finished(a, c, 0, 0, "Grupo A"),
                finished(b, c, 3, 1, "Grupo A"));

        List<GroupStanding> standings = GroupStandingsCalculator.computeAll(matches, Set.of()).get(0).standings();

        GroupStanding aStanding = standings.stream().filter(s -> s.teamId().equals(a)).findFirst().orElseThrow();
        assertEquals(2, aStanding.played());
        assertEquals(1, aStanding.won());
        assertEquals(1, aStanding.drawn());
        assertEquals(0, aStanding.lost());
        assertEquals(2, aStanding.goalsFor());
        assertEquals(1, aStanding.goalsAgainst());
        assertEquals(1, aStanding.goalDifference());
        assertEquals(4, aStanding.points());
    }

    @Test
    void table_ordenaPorPuntosDgYGf() {
        // a: 1 victoria (3 pts) | b: 1 empate + 1 derrota (1 pt) | c: 1 empate + 1 derrota (1pt, peor GF que b)
        List<Match> matches = List.of(
                finished(a, b, 3, 0, "Grupo A"),
                finished(b, c, 1, 1, "Grupo A"),
                finished(a, c, 2, 0, "Grupo A"));

        List<GroupStanding> standings = GroupStandingsCalculator.computeAll(matches, Set.of()).get(0).standings();

        assertEquals(a, standings.get(0).teamId());
        assertEquals(1, standings.get(0).position());
    }

    @Test
    void table_empatePorPuntosYDgSeResuelvePorEnfrentamientoDirecto() {
        // a y b terminan con los mismos Pts/DG/GF globales (4/0/1 cada uno),
        // pero a le ganó a b 1-0 en el cruce directo -> a debe quedar primero.
        List<Match> matches = List.of(
                finished(a, b, 1, 0, "Grupo A"),
                finished(a, c, 0, 1, "Grupo A"),
                finished(a, d, 0, 0, "Grupo A"),
                finished(b, c, 1, 0, "Grupo A"),
                finished(b, d, 0, 0, "Grupo A"),
                finished(c, d, 1, 0, "Grupo A"));

        List<GroupStanding> standings = GroupStandingsCalculator.computeAll(matches, Set.of()).get(0).standings();

        GroupStanding aStanding = standings.stream().filter(s -> s.teamId().equals(a)).findFirst().orElseThrow();
        GroupStanding bStanding = standings.stream().filter(s -> s.teamId().equals(b)).findFirst().orElseThrow();
        assertEquals(4, aStanding.points());
        assertEquals(4, bStanding.points());
        assertEquals(aStanding.points(), bStanding.points());
        assertEquals(aStanding.goalDifference(), bStanding.goalDifference());
        assertEquals(aStanding.goalsFor(), bStanding.goalsFor());

        int aPos = standings.indexOf(aStanding);
        int bPos = standings.indexOf(bStanding);
        assertTrue(aPos < bPos, "a debe quedar antes que b por el cruce directo");
    }

    @Test
    void table_empateTotalSinDesempatePosible_esDeterministaPorIdDeEquipo() {
        List<Match> matches = List.of(pending(a, b, "Grupo A"));

        List<GroupStanding> standings1 = GroupStandingsCalculator.computeAll(matches, Set.of()).get(0).standings();
        List<GroupStanding> standings2 = GroupStandingsCalculator.computeAll(matches, Set.of()).get(0).standings();

        assertEquals(standings1.get(0).teamId(), standings2.get(0).teamId());
    }

    @Test
    void table_walkover_acreditaVictoriaAlEquipoNoDescalificadoSinGoles() {
        List<Match> matches = List.of(noShow(a, b, "Grupo A"));

        List<GroupStanding> standings = GroupStandingsCalculator.computeAll(matches, Set.of(b)).get(0).standings();

        GroupStanding aStanding = standings.stream().filter(s -> s.teamId().equals(a)).findFirst().orElseThrow();
        GroupStanding bStanding = standings.stream().filter(s -> s.teamId().equals(b)).findFirst().orElseThrow();

        assertEquals(1, aStanding.played());
        assertEquals(1, aStanding.won());
        assertEquals(3, aStanding.points());
        assertEquals(0, aStanding.goalsFor());

        assertEquals(1, bStanding.played());
        assertEquals(1, bStanding.lost());
        assertEquals(0, bStanding.points());
    }

    @Test
    void table_walkover_ambosEquiposDescalificados_seIgnora() {
        List<Match> matches = List.of(noShow(a, b, "Grupo A"));

        List<GroupStanding> standings = GroupStandingsCalculator.computeAll(matches, Set.of(a, b)).get(0).standings();

        standings.forEach(s -> assertEquals(0, s.played()));
    }

    @Test
    void table_walkover_ningunoDescalificado_seIgnoraPorAmbiguo() {
        List<Match> matches = List.of(noShow(a, b, "Grupo A"));

        List<GroupStanding> standings = GroupStandingsCalculator.computeAll(matches, Set.of()).get(0).standings();

        standings.forEach(s -> assertEquals(0, s.played()));
    }

    @Test
    void table_walkover_conAusenteIdEnElPartido_acreditaAlPresenteAunqueIneligibleTeamIdsVengaVacio() {
        // A diferencia de table_walkover_ningunoDescalificado_seIgnoraPorAmbiguo: acá el
        // partido ya sabe quién faltó (ausenteId de Matches, ver ProcessMatchResult), así
        // que no depende de ineligibleTeamIds para resolverse.
        List<Match> matches = List.of(noShowWithAbsentTeam(a, b, b, "Grupo A"));

        List<GroupStanding> standings = GroupStandingsCalculator.computeAll(matches, Set.of()).get(0).standings();

        GroupStanding aStanding = standings.stream().filter(s -> s.teamId().equals(a)).findFirst().orElseThrow();
        GroupStanding bStanding = standings.stream().filter(s -> s.teamId().equals(b)).findFirst().orElseThrow();
        assertEquals(3, aStanding.points());
        assertEquals(0, bStanding.points());
    }

    @Test
    void table_walkover_absentTeamIdDelPartidoTienePrioridadSobreIneligibleTeamIds() {
        // El partido dice que el ausente es 'a'; aunque ineligibleTeamIds diga 'b', gana el
        // dato directo del propio partido.
        List<Match> matches = List.of(noShowWithAbsentTeam(a, b, a, "Grupo A"));

        List<GroupStanding> standings = GroupStandingsCalculator.computeAll(matches, Set.of(b)).get(0).standings();

        GroupStanding aStanding = standings.stream().filter(s -> s.teamId().equals(a)).findFirst().orElseThrow();
        GroupStanding bStanding = standings.stream().filter(s -> s.teamId().equals(b)).findFirst().orElseThrow();
        assertEquals(0, aStanding.points());
        assertEquals(3, bStanding.points());
    }

    @Test
    void table_gruposDeCuatro_ordenaLosCuatroEquiposPorDgCuandoLosPuntosEmpatan() {
        // Round robin completo de 4 equipos. a y b quedan empatados en Pts (7),
        // pero a tiene mejor DG (5 vs 2) -> a primero. c y d empatan en Pts (1)
        // con el mismo GF (2), pero d tiene peor DG -> c por delante de d.
        List<Match> matches = List.of(
                finished(a, d, 3, 0, "Grupo A"),
                finished(b, c, 1, 0, "Grupo A"),
                finished(a, c, 2, 0, "Grupo A"),
                finished(d, b, 0, 1, "Grupo A"),
                finished(a, b, 1, 1, "Grupo A"),
                finished(c, d, 2, 2, "Grupo A"));

        List<GroupStanding> standings = GroupStandingsCalculator.computeAll(matches, Set.of()).get(0).standings();

        assertEquals(4, standings.size());
        assertEquals(List.of(1, 2, 3, 4), standings.stream().map(GroupStanding::position).toList());
        assertEquals(List.of(a, b, c, d), standings.stream().map(GroupStanding::teamId).toList());
    }
}
