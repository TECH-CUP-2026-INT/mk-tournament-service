package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.MatchDefinitionPort.MatchDefinition;

import java.util.UUID;

/**
 * Arma la {@link MatchDefinition} a partir del emparejamiento (Match), la
 * programación (ScheduledMatch) y el snapshot de nombres de equipo del
 * torneo. Compartido por ScheduleMatchService (primer envío) y
 * ResendMatchDefinitionService (reenvío manual) para no duplicar el mapeo.
 */
final class MatchDefinitionFactory {

    private MatchDefinitionFactory() {}

    static MatchDefinition build(Tournament tournament, Match match, ScheduledMatch scheduledMatch) {
        return new MatchDefinition(
                match.getMatchId(),
                tournament.getId(),
                match.getPhase(),
                match.getHomeTeamId(),
                match.getAwayTeamId(),
                resolveTeamName(tournament, match.getHomeTeamId()),
                resolveTeamName(tournament, match.getAwayTeamId()),
                scheduledMatch.getMatchDate(),
                scheduledMatch.getMatchTime(),
                scheduledMatch.getRefereeId(),
                scheduledMatch.getCourtId());
    }

    private static String resolveTeamName(Tournament tournament, UUID teamId) {
        return tournament.getEnrollments().stream()
                .filter(e -> e.getTeamId().equals(teamId))
                .map(Enrollment::getTeamName)
                .findFirst()
                .orElse(null);
    }
}
