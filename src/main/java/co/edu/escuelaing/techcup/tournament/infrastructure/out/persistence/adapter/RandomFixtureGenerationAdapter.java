package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.FixtureGenerationPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Implementación de {@link FixtureGenerationPort}: genera la estructura
 * inicial de partidos de forma aleatoria según el formato del torneo.
 * Decisión de negocio: la generación de emparejamientos y encuentros es
 * siempre aleatoria, no se integra ningún proveedor externo.
 *
 * El {@link Random} usado para el sorteo es inyectable (con un {@link Random}
 * sin semilla por defecto en producción) para que los tests puedan pasar uno
 * con semilla fija y verificar un resultado determinista.
 *
 * Tamaño de grupo (4) y regla de bye para cantidad impar de equipos (el
 * último equipo tras el sorteo queda sin partido en la ronda inicial) son
 * decisiones "pending" en la historia, tomadas aquí como placeholder.
 */
@Component
public class RandomFixtureGenerationAdapter implements FixtureGenerationPort {

    private static final int GROUP_SIZE = 4;

    private final Random random;

    public RandomFixtureGenerationAdapter() {
        this(new Random());
    }

    RandomFixtureGenerationAdapter(Random random) {
        this.random = random;
    }

    @Override
    public List<Match> generateFixture(FixtureGenerationRequest request) {
        List<UUID> shuffledTeams = new ArrayList<>(request.approvedTeamIds());
        Collections.shuffle(shuffledTeams, random);

        return switch (request.format()) {
            case BRACKETS -> generateBracketRound(shuffledTeams);
            case LEAGUE -> generateRoundRobin(shuffledTeams);
            case GROUPS -> generateGroupPhase(shuffledTeams);
        };
    }

    private List<Match> generateBracketRound(List<UUID> teams) {
        List<Match> matches = new ArrayList<>();
        int i = 0;
        while (i + 1 < teams.size()) {
            matches.add(newMatch(teams.get(i), teams.get(i + 1)));
            i += 2;
        }
        return matches;
    }

    private List<Match> generateRoundRobin(List<UUID> teams) {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                matches.add(newMatch(teams.get(i), teams.get(j)));
            }
        }
        return matches;
    }

    private List<Match> generateGroupPhase(List<UUID> teams) {
        List<Match> matches = new ArrayList<>();
        for (int start = 0; start < teams.size(); start += GROUP_SIZE) {
            int end = Math.min(start + GROUP_SIZE, teams.size());
            matches.addAll(generateRoundRobin(teams.subList(start, end)));
        }
        return matches;
    }

    private Match newMatch(UUID homeTeamId, UUID awayTeamId) {
        return new Match(UUID.randomUUID(), homeTeamId, awayTeamId, MatchStatus.PENDING);
    }
}
