package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.service.Match;
import co.edu.escuelaing.techcup.tournament.service.MatchStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.ports.FixtureGenerationPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Implementación local de {@link FixtureGenerationPort} usada mientras se
 * define el proveedor real de la API externa de generación de fixtures
 * (Technical Constraints de la historia: "External API provider is TBD").
 * Genera la estructura inicial de partidos de forma aleatoria según las
 * reglas que la historia sí especifica; se reemplaza por un cliente HTTP
 * real implementando este mismo puerto, sin tocar el dominio ni el use case.
 *
 * Tamaño de grupo (4) y regla de bye para cantidad impar de equipos (el
 * último equipo tras el sorteo queda sin partido en la ronda inicial) son
 * decisiones "pending" en la historia, tomadas aquí como placeholder.
 */
@Component
public class RandomFixtureGenerationAdapter implements FixtureGenerationPort {

    private static final int GROUP_SIZE = 4;

    @Override
    public List<Match> generateFixture(FixtureGenerationRequest request) {
        List<String> shuffledTeams = new ArrayList<>(request.approvedTeamIds());
        Collections.shuffle(shuffledTeams);

        return switch (request.format()) {
            case BRACKETS -> generateBracketRound(shuffledTeams);
            case LEAGUE -> generateRoundRobin(shuffledTeams);
            case GROUPS -> generateGroupPhase(shuffledTeams);
        };
    }

    private List<Match> generateBracketRound(List<String> teams) {
        List<Match> matches = new ArrayList<>();
        int i = 0;
        while (i + 1 < teams.size()) {
            matches.add(newMatch(teams.get(i), teams.get(i + 1)));
            i += 2;
        }
        return matches;
    }

    private List<Match> generateRoundRobin(List<String> teams) {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                matches.add(newMatch(teams.get(i), teams.get(j)));
            }
        }
        return matches;
    }

    private List<Match> generateGroupPhase(List<String> teams) {
        List<Match> matches = new ArrayList<>();
        for (int start = 0; start < teams.size(); start += GROUP_SIZE) {
            int end = Math.min(start + GROUP_SIZE, teams.size());
            matches.addAll(generateRoundRobin(teams.subList(start, end)));
        }
        return matches;
    }

    private Match newMatch(String homeTeamId, String awayTeamId) {
        return new Match(UUID.randomUUID().toString(), homeTeamId, awayTeamId, MatchStatus.PENDING);
    }
}
