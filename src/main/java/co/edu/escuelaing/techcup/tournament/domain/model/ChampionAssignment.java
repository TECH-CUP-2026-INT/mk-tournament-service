package co.edu.escuelaing.techcup.tournament.domain.model;

import java.util.UUID;

/**
 * Resultado de asignar el campeón de un torneo: el equipo ganador, el
 * subcampeón y cómo se resolvió el desempate (tiempo reglamentario o penales).
 */
public record ChampionAssignment(UUID championTeamId, UUID runnerUpTeamId, ChampionResolution resolution) {}
