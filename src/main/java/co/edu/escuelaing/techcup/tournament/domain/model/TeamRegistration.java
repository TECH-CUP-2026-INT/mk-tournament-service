package co.edu.escuelaing.techcup.tournament.domain.model;

public class TeamRegistration {

    private String teamId;
    private String teamName;
    private RegistrationStatus registrationStatus;
    private int points;

    public TeamRegistration() {}

    public TeamRegistration(String teamId, String teamName, RegistrationStatus registrationStatus) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.registrationStatus = registrationStatus;
        this.points = 0;
    }

    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public RegistrationStatus getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(RegistrationStatus registrationStatus) { this.registrationStatus = registrationStatus; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
