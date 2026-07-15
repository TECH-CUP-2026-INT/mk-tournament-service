package co.edu.escuelaing.techcup.tournament.domain.model;

import java.util.List;

/**
 * Resultado de verificar si un torneo está listo para pasar a la fase de
 * preparación (mínimo de equipos aprobados), y qué requisitos faltan si no lo está.
 */
public class PreparationResult {

    private final boolean readyToActivate;
    private final List<String> missingRequirements;
    private final long approvedTeamsCount;

    public PreparationResult(boolean readyToActivate, List<String> missingRequirements, long approvedTeamsCount) {
        this.readyToActivate = readyToActivate;
        this.missingRequirements = missingRequirements;
        this.approvedTeamsCount = approvedTeamsCount;
    }

    public boolean isReadyToActivate() { return readyToActivate; }
    public List<String> getMissingRequirements() { return missingRequirements; }
    public long getApprovedTeamsCount() { return approvedTeamsCount; }
}
