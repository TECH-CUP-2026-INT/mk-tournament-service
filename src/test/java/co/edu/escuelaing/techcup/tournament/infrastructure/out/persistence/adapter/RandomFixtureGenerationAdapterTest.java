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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomFixtureGenerationAdapterTest {

    private final FixtureGenerationPort adapter = new RandomFixtureGenerationAdapter();

    private Set<String> teamsInMatches(List<Match> matches) {
        Set<String> teams = new HashSet<>();
        matches.forEach(m -> {
            teams.add(m.getHomeTeamId());
            teams.add(m.getAwayTeamId());
        });
        return teams;
    }

    @Test
    void brackets_conCuatroEquipos_generaDosPartidosSinRepetirEquipos() {
        List<String> approved = List.of("e1", "e2", "e3", "e4");

        List<Match> matches = adapter.generateFixture(
                new FixtureGenerationRequest("t1", approved, TournamentFormat.BRACKETS));

        assertEquals(2, matches.size());
        assertEquals(new HashSet<>(approved), teamsInMatches(matches));
    }

    @Test
    void brackets_conCantidadImpar_ultimoEquipoQuedaSinPartidoEnRondaInicial() {
        List<String> approved = List.of("e1", "e2", "e3");

        List<Match> matches = adapter.generateFixture(
                new FixtureGenerationRequest("t1", approved, TournamentFormat.BRACKETS));

        assertEquals(1, matches.size());
        assertTrue(approved.containsAll(teamsInMatches(matches)));
    }

    @Test
    void league_conTresEquipos_generaTodosLosCrucesPosibles() {
        List<String> approved = List.of("e1", "e2", "e3");

        List<Match> matches = adapter.generateFixture(
                new FixtureGenerationRequest("t1", approved, TournamentFormat.LEAGUE));

        assertEquals(3, matches.size());
        assertEquals(new HashSet<>(approved), teamsInMatches(matches));
    }

    @Test
    void groups_conCincoEquipos_generaGruposDeHastaCuatroConRoundRobinInterno() {
        List<String> approved = List.of("e1", "e2", "e3", "e4", "e5");

        List<Match> matches = adapter.generateFixture(
                new FixtureGenerationRequest("t1", approved, TournamentFormat.GROUPS));

        // grupo de 4 -> 6 partidos (round robin), grupo de 1 -> 0 partidos
        assertEquals(6, matches.size());
        assertTrue(approved.containsAll(teamsInMatches(matches)));
    }

    @Test
    void brackets_conRngConSemillaFija_esDeterminista() {
        FixtureGenerationPort seededAdapter = new RandomFixtureGenerationAdapter(new Random(42));
        List<String> approved = List.of("e1", "e2", "e3", "e4");

        List<Match> matches = seededAdapter.generateFixture(
                new FixtureGenerationRequest("t1", approved, TournamentFormat.BRACKETS));

        assertEquals(2, matches.size());
        assertEquals("e4", matches.get(0).getHomeTeamId());
        assertEquals("e2", matches.get(0).getAwayTeamId());
        assertEquals("e1", matches.get(1).getHomeTeamId());
        assertEquals("e3", matches.get(1).getAwayTeamId());
    }
}
