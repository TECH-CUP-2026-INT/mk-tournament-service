package co.edu.escuelaing.techcup.tournament.domain.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Recalcula desde cero la tabla de posiciones de cada grupo de la fase
 * clasificatoria a partir de sus partidos: victoria 3, empate 1, derrota 0;
 * ordena por Pts -> DG -> GF -> enfrentamiento directo. Partidos sin
 * groupName (formatos sin fase de grupos, o partidos de la llave eliminatoria)
 * se ignoran.
 * <p>
 * Un partido FINISHED_NO_SHOW (walkover: ver {@link Tournament#disqualifyTeam}
 * / {@link Tournament#removeTeam}) cuenta como victoria administrativa del
 * equipo que NO está en {@code ineligibleTeamIds}, sin sumar goles a ninguno
 * de los dos; si ambos equipos (o ninguno) están en ese conjunto, el partido
 * no se puede resolver y se ignora.
 */
public final class GroupStandingsCalculator {

    private GroupStandingsCalculator() {}

    /**
     * Equipos cuyos partidos walkover (FINISHED_NO_SHOW) no deben acreditarse como
     * victoria propia: descalificados (siguen en teams(), ver
     * {@link Tournament#disqualifyTeam}) o removidos por completo del torneo (ya
     * no están en teams(), ver {@link Tournament#removeTeam}).
     */
    public static Set<UUID> ineligibleTeamIds(List<TeamRegistration> teams, List<Match> matches) {
        Set<UUID> registeredTeamIds = new HashSet<>();
        Set<UUID> ineligible = new HashSet<>();
        for (TeamRegistration team : teams) {
            registeredTeamIds.add(team.getTeamId());
            if (team.getRegistrationStatus() == RegistrationStatus.DISQUALIFIED) {
                ineligible.add(team.getTeamId());
            }
        }
        for (Match match : matches) {
            if (!registeredTeamIds.contains(match.getHomeTeamId())) {
                ineligible.add(match.getHomeTeamId());
            }
            if (!registeredTeamIds.contains(match.getAwayTeamId())) {
                ineligible.add(match.getAwayTeamId());
            }
        }
        return ineligible;
    }

    public static List<GroupTable> computeAll(List<Match> matches, Set<UUID> ineligibleTeamIds) {
        Map<String, List<Match>> byGroup = matches.stream()
                .filter(m -> m.getGroupName() != null)
                .collect(Collectors.groupingBy(Match::getGroupName, LinkedHashMap::new, Collectors.toList()));

        return byGroup.entrySet().stream()
                .map(e -> new GroupTable(e.getKey(), table(e.getValue(), ineligibleTeamIds)))
                .sorted(Comparator.comparing(GroupTable::groupName))
                .toList();
    }

    static List<GroupStanding> table(List<Match> groupMatches, Set<UUID> ineligibleTeamIds) {
        Set<UUID> teamIds = new LinkedHashSet<>();
        for (Match m : groupMatches) {
            teamIds.add(m.getHomeTeamId());
            teamIds.add(m.getAwayTeamId());
        }

        Map<UUID, Accumulator> stats = new HashMap<>();
        for (UUID id : teamIds) stats.put(id, new Accumulator());

        for (Match m : groupMatches) {
            if (m.getStatus() == MatchStatus.FINISHED) {
                applyFinishedResult(stats, m);
            } else if (m.getStatus() == MatchStatus.FINISHED_NO_SHOW) {
                applyWalkoverResult(stats, m, ineligibleTeamIds);
            }
        }

        List<GroupStanding> base = teamIds.stream()
                .map(id -> stats.get(id).toStanding(id))
                .toList();

        List<GroupStanding> sorted = sortWithTiebreakers(base, groupMatches);

        List<GroupStanding> withPositions = new ArrayList<>(sorted.size());
        for (int i = 0; i < sorted.size(); i++) {
            withPositions.add(sorted.get(i).withPosition(i + 1));
        }
        return withPositions;
    }

    private static void applyFinishedResult(Map<UUID, Accumulator> stats, Match m) {
        Accumulator home = stats.get(m.getHomeTeamId());
        Accumulator away = stats.get(m.getAwayTeamId());
        home.played++;
        away.played++;
        home.goalsFor += m.getHomeScore();
        home.goalsAgainst += m.getAwayScore();
        away.goalsFor += m.getAwayScore();
        away.goalsAgainst += m.getHomeScore();
        if (m.getHomeScore() > m.getAwayScore()) {
            home.won++;
            away.lost++;
        } else if (m.getAwayScore() > m.getHomeScore()) {
            away.won++;
            home.lost++;
        } else {
            home.drawn++;
            away.drawn++;
        }
    }

    private static void applyWalkoverResult(Map<UUID, Accumulator> stats, Match m, Set<UUID> ineligibleTeamIds) {
        boolean homeIneligible = ineligibleTeamIds.contains(m.getHomeTeamId());
        boolean awayIneligible = ineligibleTeamIds.contains(m.getAwayTeamId());
        if (homeIneligible == awayIneligible) {
            return;
        }

        Accumulator winner = stats.get(homeIneligible ? m.getAwayTeamId() : m.getHomeTeamId());
        Accumulator loser = stats.get(homeIneligible ? m.getHomeTeamId() : m.getAwayTeamId());
        winner.played++;
        winner.won++;
        loser.played++;
        loser.lost++;
    }

    private static List<GroupStanding> sortWithTiebreakers(List<GroupStanding> base, List<Match> groupMatches) {
        List<GroupStanding> sorted = new ArrayList<>(base);
        sorted.sort(primaryComparator());

        List<GroupStanding> result = new ArrayList<>();
        int i = 0;
        while (i < sorted.size()) {
            int j = i + 1;
            while (j < sorted.size() && tiedOnPrimaryCriteria(sorted.get(i), sorted.get(j))) {
                j++;
            }
            List<GroupStanding> cluster = sorted.subList(i, j);
            result.addAll(cluster.size() > 1 ? breakTieByHeadToHead(cluster, groupMatches) : cluster);
            i = j;
        }
        return result;
    }

    private static Comparator<GroupStanding> primaryComparator() {
        return Comparator.comparingInt(GroupStanding::points).reversed()
                .thenComparing(Comparator.comparingInt(GroupStanding::goalDifference).reversed())
                .thenComparing(Comparator.comparingInt(GroupStanding::goalsFor).reversed());
    }

    private static boolean tiedOnPrimaryCriteria(GroupStanding a, GroupStanding b) {
        return a.points() == b.points()
                && a.goalDifference() == b.goalDifference()
                && a.goalsFor() == b.goalsFor();
    }

    /**
     * Desempate por enfrentamiento directo: recalcula una mini-tabla (mismo
     * criterio Pts -> DG -> GF) usando solo los partidos entre los equipos
     * empatados. Si persiste el empate (p. ej. triangular cíclico de
     * resultados), se ordena por id de equipo para un resultado determinista.
     */
    private static List<GroupStanding> breakTieByHeadToHead(List<GroupStanding> cluster, List<Match> groupMatches) {
        Set<UUID> clusterIds = cluster.stream().map(GroupStanding::teamId).collect(Collectors.toSet());
        List<Match> headToHeadMatches = groupMatches.stream()
                .filter(m -> clusterIds.contains(m.getHomeTeamId()) && clusterIds.contains(m.getAwayTeamId()))
                .toList();

        Map<UUID, Accumulator> subStats = new HashMap<>();
        for (UUID id : clusterIds) subStats.put(id, new Accumulator());
        for (Match m : headToHeadMatches) {
            if (m.getStatus() == MatchStatus.FINISHED) {
                applyFinishedResult(subStats, m);
            }
        }

        Map<UUID, GroupStanding> subStandingByTeam = clusterIds.stream()
                .collect(Collectors.toMap(id -> id, id -> subStats.get(id).toStanding(id)));

        List<GroupStanding> result = new ArrayList<>(cluster);
        result.sort(Comparator
                .<GroupStanding>comparingInt(gs -> subStandingByTeam.get(gs.teamId()).points()).reversed()
                .thenComparing(Comparator.<GroupStanding>comparingInt(
                        gs -> subStandingByTeam.get(gs.teamId()).goalDifference()).reversed())
                .thenComparing(Comparator.<GroupStanding>comparingInt(
                        gs -> subStandingByTeam.get(gs.teamId()).goalsFor()).reversed())
                .thenComparing(gs -> gs.teamId().toString()));
        return result;
    }

    private static final class Accumulator {
        int played;
        int won;
        int drawn;
        int lost;
        int goalsFor;
        int goalsAgainst;

        GroupStanding toStanding(UUID teamId) {
            int points = won * 3 + drawn;
            int goalDifference = goalsFor - goalsAgainst;
            return new GroupStanding(0, teamId, played, won, drawn, lost, goalsFor, goalsAgainst, goalDifference, points);
        }
    }
}
