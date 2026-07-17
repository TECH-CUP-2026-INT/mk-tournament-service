package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.FixtureGenerationPort;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
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
            case BRACKETS -> generateBracketRound(shuffledTeams, request.tournamentId());
            case LEAGUE -> generateRoundRobin(shuffledTeams, request.tournamentId());
            case GROUPS -> generateGroupPhase(shuffledTeams, request.tournamentId());
        };
    }

    private List<Match> generateBracketRound(List<UUID> teams, UUID tournamentId) {
        List<Match> matches = new ArrayList<>();
        int i = 0;
        while (i + 1 < teams.size()) {
            matches.add(newMatch(teams.get(i), teams.get(i + 1), tournamentId));
            i += 2;
        }
        return matches;
    }

    private List<Match> generateRoundRobin(List<UUID> teams, UUID tournamentId) {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                matches.add(newMatch(teams.get(i), teams.get(j), tournamentId));
            }
        }
        return matches;
    }

    /**
     * Arma los grupos de 4 (sorteo ya aplicado por el shuffle de generateFixture)
     * y genera el round-robin interno de cada uno, con nombre de grupo ("Grupo A",
     * "Grupo B", ...) y jornada (1..3) vía el método del círculo. Un grupo residual
     * de menos de 4 equipos (total no múltiplo de 4) usa todos-contra-todos sin
     * jornada asignada; no debería ocurrir en producción porque el llamador valida
     * N ∈ {8,16,32} antes de invocar este puerto para el formato de grupos.
     */
    private List<Match> generateGroupPhase(List<UUID> teams, UUID tournamentId) {
        List<Match> matches = new ArrayList<>();
        int groupIndex = 0;
        for (int start = 0; start < teams.size(); start += GROUP_SIZE) {
            int end = Math.min(start + GROUP_SIZE, teams.size());
            List<UUID> group = teams.subList(start, end);
            String groupName = "Grupo " + (char) ('A' + groupIndex);
            matches.addAll(generateGroupRoundRobin(group, groupName, tournamentId));
            groupIndex++;
        }
        return matches;
    }

    private List<Match> generateGroupRoundRobin(List<UUID> group, String groupName, UUID tournamentId) {
        if (group.size() == GROUP_SIZE) {
            return circleMethodRoundRobin(group, groupName, tournamentId);
        }
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < group.size(); i++) {
            for (int j = i + 1; j < group.size(); j++) {
                matches.add(newGroupMatch(group.get(i), group.get(j), groupName, null, tournamentId));
            }
        }
        return matches;
    }

    /**
     * Método del círculo: fija el primer equipo y rota el resto para producir
     * n-1 jornadas de n/2 partidos cada una, sin repetir cruces.
     */
    private List<Match> circleMethodRoundRobin(List<UUID> group, String groupName, UUID tournamentId) {
        UUID[] rotation = group.toArray(new UUID[0]);
        int n = rotation.length;
        List<Match> matches = new ArrayList<>();
        for (int round = 1; round <= n - 1; round++) {
            for (int i = 0; i < n / 2; i++) {
                matches.add(newGroupMatch(rotation[i], rotation[n - 1 - i], groupName, round, tournamentId));
            }
            UUID last = rotation[n - 1];
            System.arraycopy(rotation, 1, rotation, 2, n - 2);
            rotation[1] = last;
        }
        return matches;
    }

    private Match newGroupMatch(UUID homeTeamId, UUID awayTeamId, String groupName, Integer matchday, UUID tournamentId) {
        return Match.builder()
                .matchId(UUID.randomUUID())
                .homeTeamId(homeTeamId)
                .awayTeamId(awayTeamId)
                .status(MatchStatus.PENDING)
                .active(true)
                .groupName(groupName)
                .matchday(matchday)
                .phase(MatchPhase.GRUPOS)
                .tournamentId(tournamentId)
                .build();
    }

    private Match newMatch(UUID homeTeamId, UUID awayTeamId, UUID tournamentId) {
        return Match.builder()
                .matchId(UUID.randomUUID())
                .homeTeamId(homeTeamId)
                .awayTeamId(awayTeamId)
                .status(MatchStatus.PENDING)
                .active(true)
                .tournamentId(tournamentId)
                .build();
    }
}
