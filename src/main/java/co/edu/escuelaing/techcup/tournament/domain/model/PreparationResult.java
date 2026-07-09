package co.edu.escuelaing.techcup.tournament.domain.model;

import java.util.List;

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
