package co.edu.escuelaing.techcup.tournament.domain.model;

import java.util.List;

/**
 * Tabla de posiciones de un grupo de la fase clasificatoria.
 */
public record GroupTable(String groupName, List<GroupStanding> standings) {}
