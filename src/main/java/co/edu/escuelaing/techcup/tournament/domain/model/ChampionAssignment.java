package co.edu.escuelaing.techcup.tournament.domain.model;

/**
 * Resultado de asignar el campeón de un torneo: el equipo ganador y cómo se
 * resolvió el desempate (tiempo reglamentario o penales).
 */
public record ChampionAssignment(String championTeamId, ChampionResolution resolution) {}
