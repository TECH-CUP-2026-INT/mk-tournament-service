package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.FixtureGenerationPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.FixtureGenerationPort.FixtureGenerationRequest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomFixtureGenerationAdapterTest {

    private final FixtureGenerationPort adapter = new RandomFixtureGenerationAdapter();
    private final UUID tournamentId = UUID.randomUUID();
    private final UUID e1 = UUID.randomUUID();
    private final UUID e2 = UUID.randomUUID();
    private final UUID e3 = UUID.randomUUID();
    private final UUID e4 = UUID.randomUUID();
    private final UUID e5 = UUID.randomUUID();
    private final UUID e6 = UUID.randomUUID();
    private final UUID e7 = UUID.randomUUID();
    private final UUID e8 = UUID.randomUUID();

    private Set<UUID> teamsInMatches(List<Match> matches) {
        Set<UUID> teams = new HashSet<>();
        matches.forEach(m -> {
            teams.add(m.getHomeTeamId());
            teams.add(m.getAwayTeamId());
        });
        return teams;
    }

    @Test
    void brackets_conCuatroEquipos_generaDosPartidosSinRepetirEquipos() {
        List<UUID> approved = List.of(e1, e2, e3, e4);

        List<Match> matches = adapter.generateFixture(
                new FixtureGenerationRequest(tournamentId, approved, TournamentFormat.BRACKETS));

        assertEquals(2, matches.size());
        assertEquals(new HashSet<>(approved), teamsInMatches(matches));
    }

    @Test
    void brackets_conCantidadImpar_ultimoEquipoQuedaSinPartidoEnRondaInicial() {
        List<UUID> approved = List.of(e1, e2, e3);

        List<Match> matches = adapter.generateFixture(
                new FixtureGenerationRequest(tournamentId, approved, TournamentFormat.BRACKETS));

        assertEquals(1, matches.size());
        assertTrue(approved.containsAll(teamsInMatches(matches)));
    }

    @Test
    void league_conTresEquipos_generaTodosLosCrucesPosibles() {
        List<UUID> approved = List.of(e1, e2, e3);

        List<Match> matches = adapter.generateFixture(
                new FixtureGenerationRequest(tournamentId, approved, TournamentFormat.LEAGUE));

        assertEquals(3, matches.size());
        assertEquals(new HashSet<>(approved), teamsInMatches(matches));
    }

    @Test
    void groups_conCincoEquipos_generaGruposDeHastaCuatroConRoundRobinInterno() {
        List<UUID> approved = List.of(e1, e2, e3, e4, e5);

        List<Match> matches = adapter.generateFixture(
                new FixtureGenerationRequest(tournamentId, approved, TournamentFormat.GROUPS));

        // grupo de 4 -> 6 partidos (round robin), grupo de 1 -> 0 partidos
        assertEquals(6, matches.size());
        assertTrue(approved.containsAll(teamsInMatches(matches)));
    }

    @Test
    void groups_conOchoEquipos_asignaDosGruposDeCuatroConNombreYJornada() {
        List<UUID> approved = List.of(e1, e2, e3, e4, e5, e6, e7, e8);

        List<Match> matches = adapter.generateFixture(
                new FixtureGenerationRequest(tournamentId, approved, TournamentFormat.GROUPS));

        assertEquals(12, matches.size());
        Set<String> groupNames = matches.stream().map(Match::getGroupName).collect(Collectors.toSet());
        assertEquals(Set.of("Grupo A", "Grupo B"), groupNames);

        for (String groupName : groupNames) {
            List<Match> groupMatches = matches.stream().filter(m -> groupName.equals(m.getGroupName())).toList();
            assertEquals(6, groupMatches.size());
            assertEquals(Set.of(1, 2, 3), groupMatches.stream().map(Match::getMatchday).collect(Collectors.toSet()));

            Set<UUID> groupTeams = teamsInMatches(groupMatches);
            assertEquals(4, groupTeams.size());

            // sin cruces repetidos dentro del grupo
            Set<Set<UUID>> pairs = groupMatches.stream()
                    .map(m -> Set.of(m.getHomeTeamId(), m.getAwayTeamId()))
                    .collect(Collectors.toSet());
            assertEquals(6, pairs.size());
        }
    }

    @Test
    void brackets_conRngConSemillaFija_esDeterminista() {
        FixtureGenerationPort seededAdapter = new RandomFixtureGenerationAdapter(new Random(42));
        List<UUID> approved = List.of(e1, e2, e3, e4);

        List<Match> matches = seededAdapter.generateFixture(
                new FixtureGenerationRequest(tournamentId, approved, TournamentFormat.BRACKETS));

        assertEquals(2, matches.size());
        assertEquals(e4, matches.get(0).getHomeTeamId());
        assertEquals(e2, matches.get(0).getAwayTeamId());
        assertEquals(e1, matches.get(1).getHomeTeamId());
        assertEquals(e3, matches.get(1).getAwayTeamId());
    }
}
