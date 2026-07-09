package co.edu.escuelaing.techcup.tournament.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Tournament {

    private static final int MIN_TEAMS_REQUIRED = 3;

    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private int durationDays;
    private TournamentStatus status;
    private EliminationType eliminationType;
    private List<TeamRegistration> teams;
    private List<Match> matches;

    public Tournament() {
        this.teams = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.status = TournamentStatus.PREPARATION;
    }

    public Tournament(String id, String name, LocalDate startDate, LocalDate endDate, EliminationType eliminationType) {
        this();
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eliminationType = eliminationType;
        if (startDate != null && endDate != null)
            this.durationDays = (int) startDate.until(endDate).getDays();
    }

    // --- Preparación del torneo (TC-25) ---

    public PreparationResult checkPreparation() {
        List<String> missing = new ArrayList<>();

        if (startDate == null || endDate == null)
            missing.add("Fechas de inicio y fin son obligatorias");
        else if (!endDate.isAfter(startDate))
            missing.add("La fecha de fin debe ser posterior a la de inicio");

        long approvedCount = countApprovedTeams();
        if (approvedCount < MIN_TEAMS_REQUIRED)
            missing.add("Se requieren al menos " + MIN_TEAMS_REQUIRED + " equipos aprobados, faltan " + (MIN_TEAMS_REQUIRED - approvedCount));

        boolean allApprovedIncluded = teams.stream()
                .filter(t -> t.getRegistrationStatus() == RegistrationStatus.APPROVED)
                .allMatch(t -> teams.contains(t));

        boolean ready = missing.isEmpty() && allApprovedIncluded;
        return new PreparationResult(ready, missing, approvedCount);
    }

    private long countApprovedTeams() {
        return teams.stream()
                .filter(t -> t.getRegistrationStatus() == RegistrationStatus.APPROVED)
                .count();
    }

    // --- Eliminar equipo (TC-48) ---

    public List<Match> removeTeam(String teamId, RemovalReason reason) {
        if (status != TournamentStatus.ACTIVE && status != TournamentStatus.IN_PROGRESS)
            throw new TeamRemovalNotAllowedException("Solo se puede remover equipos en torneos Activos o En Progreso");

        TeamRegistration team = teams.stream()
                .filter(t -> t.getTeamId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new TeamRemovalNotAllowedException("El equipo no está inscrito en este torneo"));

        teams.remove(team);

        List<Match> affectedMatches = new ArrayList<>();
        for (Match match : matches) {
            if (match.isPending() && match.involvesteam(teamId)) {
                match.markAsNoShow();
                affectedMatches.add(match);
            }
        }

        return affectedMatches;
    }

    // --- Getters y Setters ---

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

    public List<TeamRegistration> getTeams() { return teams; }
    public void setTeams(List<TeamRegistration> teams) { this.teams = teams; }

    public List<Match> getMatches() { return matches; }
    public void setMatches(List<Match> matches) { this.matches = matches; }
}
