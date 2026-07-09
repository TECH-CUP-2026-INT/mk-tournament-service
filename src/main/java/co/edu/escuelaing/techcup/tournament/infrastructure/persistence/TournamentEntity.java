package co.edu.escuelaing.techcup.tournament.infrastructure.persistence;

import co.edu.escuelaing.techcup.tournament.domain.model.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "tournaments")
public class TournamentEntity {

    @Id
    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private int durationDays;
    private TournamentStatus status;
    private EliminationType eliminationType;
    private List<TeamRegistrationEmbedded> teams;
    private List<MatchEmbedded> matches;

    public static class TeamRegistrationEmbedded {
        private String teamId;
        private String teamName;
        private RegistrationStatus registrationStatus;
        private int points;

        public String getTeamId() { return teamId; }
        public void setTeamId(String teamId) { this.teamId = teamId; }
        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }
        public RegistrationStatus getRegistrationStatus() { return registrationStatus; }
        public void setRegistrationStatus(RegistrationStatus registrationStatus) { this.registrationStatus = registrationStatus; }
        public int getPoints() { return points; }
        public void setPoints(int points) { this.points = points; }
    }

    public static class MatchEmbedded {
        private String matchId;
        private String homeTeamId;
        private String awayTeamId;
        private MatchStatus status;

        public String getMatchId() { return matchId; }
        public void setMatchId(String matchId) { this.matchId = matchId; }
        public String getHomeTeamId() { return homeTeamId; }
        public void setHomeTeamId(String homeTeamId) { this.homeTeamId = homeTeamId; }
        public String getAwayTeamId() { return awayTeamId; }
        public void setAwayTeamId(String awayTeamId) { this.awayTeamId = awayTeamId; }
        public MatchStatus getStatus() { return status; }
        public void setStatus(MatchStatus status) { this.status = status; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
    public TournamentStatus getStatus() { return status; }
    public void setStatus(TournamentStatus status) { this.status = status; }
    public EliminationType getEliminationType() { return eliminationType; }
    public void setEliminationType(EliminationType eliminationType) { this.eliminationType = eliminationType; }
    public List<TeamRegistrationEmbedded> getTeams() { return teams; }
    public void setTeams(List<TeamRegistrationEmbedded> teams) { this.teams = teams; }
    public List<MatchEmbedded> getMatches() { return matches; }
    public void setMatches(List<MatchEmbedded> matches) { this.matches = matches; }
}
